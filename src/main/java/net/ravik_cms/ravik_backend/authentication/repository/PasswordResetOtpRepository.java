package net.ravik_cms.ravik_backend.authentication.repository;

import net.ravik_cms.ravik_backend.authentication.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, UUID> {
    Optional<PasswordResetOtp> findFirstByUserIdAndUsedAtIsNullOrderByCreatedAtDesc(UUID userId);

    @Modifying
    @Query("DELETE FROM PasswordResetOtp o WHERE o.userId = :userId AND o.usedAt IS NULL")
    void deleteAllUnusedForUser(@Param("userId") UUID userId);
}
