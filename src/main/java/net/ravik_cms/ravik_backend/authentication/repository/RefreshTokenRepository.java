package net.ravik_cms.ravik_backend.authentication.repository;

import net.ravik_cms.ravik_backend.authentication.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, java.util.UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
}
