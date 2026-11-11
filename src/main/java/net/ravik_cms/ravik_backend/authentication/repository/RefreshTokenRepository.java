package net.ravik_cms.ravik_backend.authentication.repository;

import net.ravik_cms.ravik_backend.authentication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = :revokedAt " +
            "WHERE r.userId = :userId AND r.revokedAt IS NULL")
    void revokeAllByUserId(@Param("userId") UUID userId, @Param("revokedAt") Instant revokedAt);

    @Modifying
    @Query("UPDATE RefreshToken r SET r.revokedAt = :revokedAt " +
            "WHERE r.userId = :userId AND r.revokedAt IS NULL AND r.tokenHash <> :exceptTokenHash")
    void revokeAllByUserIdExcept(@Param("userId") UUID userId, @Param("exceptTokenHash") String exceptTokenHash,
                                  @Param("revokedAt") Instant revokedAt);
}
