package net.ravik_cms.ravik_backend.authentication.service.impl;

import jakarta.transaction.Transactional;
import net.ravik_cms.ravik_backend.authentication.dto.RefreshTokenResult;
import net.ravik_cms.ravik_backend.authentication.entity.RefreshToken;
import net.ravik_cms.ravik_backend.authentication.repository.RefreshTokenRepository;
import net.ravik_cms.ravik_backend.authentication.service.RefreshTokenService;
import net.ravik_cms.ravik_backend.common.exception.InvalidTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Default {@link RefreshTokenService} implementation.
 * <p>
 * Refresh tokens are opaque, cryptographically random values. Only a SHA-256 hash of the token
 * is persisted (never the raw value, same principle as password storage), and each token is
 * single-use: {@link #rotate} revokes the presented token and issues a fresh one in the same call.
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMs;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh.expiration}") long refreshExpirationMs
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    /**
     * Generates a new refresh token for the given user and persists its hash.
     */
    @Override
    @Transactional
    public String issue(UUID userId) {
        String rawToken = generateRawToken();
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setTokenHash(hash(rawToken));
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshExpirationMs));
        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    /**
     * Validates a presented refresh token, revokes it, and issues a replacement for the same user.
     *
     * @throws InvalidTokenException if the token is unknown, already revoked, or expired
     */
    @Override
    @Transactional
    public RefreshTokenResult rotate(String rawToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.getRevokedAt() != null) {
            throw new InvalidTokenException("Refresh token has already been used");
        }
        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token has expired");
        }

        refreshToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        String newRawToken = issue(refreshToken.getUserId());
        return new RefreshTokenResult(refreshToken.getUserId(), newRawToken);
    }

    /**
     * Revokes a refresh token so it can no longer be used to obtain new access tokens. A token
     * that is unknown or already revoked is treated as a no-op rather than an error.
     */
    @Override
    @Transactional
    public void revoke(String rawToken) {
        refreshTokenRepository.findByTokenHash(hash(rawToken))
                .ifPresent(refreshToken -> {
                    refreshToken.setRevokedAt(Instant.now());
                    refreshTokenRepository.save(refreshToken);
                });
    }

    private static String generateRawToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
