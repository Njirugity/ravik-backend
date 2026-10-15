package net.ravik_cms.ravik_backend.authentication.service;

import net.ravik_cms.ravik_backend.authentication.dto.CurrentUserDto;
import net.ravik_cms.ravik_backend.authentication.dto.LoginRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginResponse;

import java.util.UUID;

public interface AuthService {
    CurrentUserDto getCurrentUser(UUID userId, UUID projectId);

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshToken);

    void logout(String refreshToken);
}
