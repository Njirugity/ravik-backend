package net.ravik_cms.ravik_backend.authentication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity;

import java.time.Instant;
import java.util.UUID;

/**
 * A single forgot-password OTP challenge for a user.
 * <p>
 * The code is stored raw (not hashed) so it can be read directly from the database if email
 * delivery fails. {@code otp} is cleared once {@code validateOtp} succeeds, so it can't be reused
 * or read back afterward — {@code verifiedAt} is what {@code resetPassword} then checks for.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetOtp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    private String otp;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts = 0;

    private Instant verifiedAt;

    private Instant usedAt;
}
