package net.ravik_cms.ravik_backend.authentication.service;

import net.ravik_cms.ravik_backend.authentication.dto.RefreshTokenResult;

import java.util.UUID;

public interface RefreshTokenService {
    String issue(UUID userId);

    RefreshTokenResult rotate(String rawToken);

    void revoke(String rawToken);
}
