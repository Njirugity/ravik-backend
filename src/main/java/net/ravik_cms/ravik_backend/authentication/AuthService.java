package net.ravik_cms.ravik_backend.authentication;

import java.util.UUID;

public interface AuthService {
    CurrentUserDto getCurrentUser(UUID userId, UUID projectId);
}
