package net.ravik_cms.ravik_backend.authentication.service.impl;

import jakarta.transaction.Transactional;
import net.ravik_cms.ravik_backend.authentication.entity.PasswordResetOtp;
import net.ravik_cms.ravik_backend.authentication.repository.PasswordResetOtpRepository;
import net.ravik_cms.ravik_backend.authentication.service.PasswordResetService;
import net.ravik_cms.ravik_backend.authentication.service.RefreshTokenService;
import net.ravik_cms.ravik_backend.common.exception.InvalidOtpException;
import net.ravik_cms.ravik_backend.common.mail.EmailSender;
import net.ravik_cms.ravik_backend.common.password.PasswordPolicy;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Default {@link PasswordResetService} implementation.
 * <p>
 * Mirrors {@link RefreshTokenServiceImpl}'s single-use-record shape, except the OTP is stored
 * raw rather than hashed, so it stays readable from the database if the email never arrives —
 * and is cleared the moment it's validated so it can't be read or replayed afterward.
 */
@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final Logger log = LoggerFactory.getLogger(PasswordResetServiceImpl.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final EmailSender emailSender;
    private final PasswordPolicy passwordPolicy;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final int otpLength;
    private final long otpExpiryMinutes;
    private final int otpMaxAttempts;

    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetOtpRepository otpRepository,
            EmailSender emailSender,
            PasswordPolicy passwordPolicy,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            @Value("${app.otp.length}") int otpLength,
            @Value("${app.otp.expiry-minutes}") long otpExpiryMinutes,
            @Value("${app.otp.max-attempts}") int otpMaxAttempts
    ) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.emailSender = emailSender;
        this.passwordPolicy = passwordPolicy;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.otpLength = otpLength;
        this.otpExpiryMinutes = otpExpiryMinutes;
        this.otpMaxAttempts = otpMaxAttempts;
    }

    /**
     * Issues and emails a fresh OTP, replacing any still-unused one for the same user. Silently
     * no-ops if the username doesn't exist or has no email on file, and swallows email-send
     * failures (logging them instead) — the OTP is still persisted either way, so it can be read
     * directly from the database if delivery fails. In every case, the caller sees the same
     * generic success response, to avoid revealing whether a username exists.
     */
    @Override
    @Transactional
    public void forgotPassword(String userName) {
        Optional<Users> userOpt = userRepository.findByUserName(userName);
        if (userOpt.isEmpty()) {
            return;
        }
        Users user = userOpt.get();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Forgot-password requested for user {} with no email on file", user.getId());
            return;
        }

        otpRepository.deleteAllUnusedForUser(user.getId());

        String otp = generateOtp();
        PasswordResetOtp record = new PasswordResetOtp();
        record.setUserId(user.getId());
        record.setOtp(otp);
        record.setExpiresAt(Instant.now().plus(otpExpiryMinutes, ChronoUnit.MINUTES));
        record.setAttempts(0);
        otpRepository.save(record);

        try {
            emailSender.send(user.getEmail(), "Your Ravik password reset code",
                    "Your password reset code is " + otp + ". It expires in " + otpExpiryMinutes + " minutes.");
        } catch (Exception e) {
            log.error("Failed to email password reset OTP to user {}", user.getId(), e);
        }
    }

    /**
     * Checks a presented OTP against the user's live code; on success clears the stored code and
     * marks the record verified so {@link #resetPassword} will accept it.
     *
     * @throws InvalidOtpException if there's no live code, it's expired, attempts are exhausted,
     *                             it was already validated, or the presented code doesn't match
     */
    // Deliberately not @Transactional: the wrong-attempt branch below must persist its
    // otpRepository.save(record) even though the method then throws — a surrounding transaction
    // would roll that save back along with the exception, silently losing the attempt count.
    @Override
    public void validateOtp(String userName, String otp) {
        Users user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired code."));
        PasswordResetOtp record = findLiveOtp(user);

        if (record.getAttempts() >= otpMaxAttempts) {
            throw new InvalidOtpException("Too many attempts. Please request a new code.");
        }
        if (record.getOtp() == null) {
            throw new InvalidOtpException("This code has already been verified.");
        }
        if (!record.getOtp().equals(otp)) {
            record.setAttempts(record.getAttempts() + 1);
            otpRepository.save(record);
            int remaining = otpMaxAttempts - record.getAttempts();
            throw new InvalidOtpException("Incorrect code. " + remaining + " attempt(s) remaining.");
        }

        record.setOtp(null);
        record.setVerifiedAt(Instant.now());
        otpRepository.save(record);
    }

    /**
     * Sets a new password for a user whose OTP has already been validated.
     *
     * @throws InvalidOtpException if there's no live code, it hasn't been verified, or the
     *                             overall window (from the original OTP's expiry) has passed
     */
    @Override
    @Transactional
    public void resetPassword(String userName, String newPassword, boolean terminateOtherSessions) {
        Users user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new InvalidOtpException("Verify your code first."));
        PasswordResetOtp record = findLiveOtp(user);

        if (record.getVerifiedAt() == null) {
            throw new InvalidOtpException("Verify your code first.");
        }

        passwordPolicy.validate(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        record.setUsedAt(Instant.now());
        otpRepository.save(record);

        if (terminateOtherSessions) {
            refreshTokenService.revokeAllForUser(user.getId());
        }
    }

    private PasswordResetOtp findLiveOtp(Users user) {
        PasswordResetOtp record = otpRepository.findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new InvalidOtpException("Invalid or expired code."));
        if (record.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidOtpException("This code has expired. Please request a new one.");
        }
        return record;
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, otpLength);
        int value = SECURE_RANDOM.nextInt(bound);
        return String.format("%0" + otpLength + "d", value);
    }
}
