package net.ravik_cms.ravik_backend.authentication.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record CurrentUserDto(
        UUID userId,
        String userName,
        String email,
        UUID projectId,
        UUID roleId,
        String roleName,
        List<String> permissions
) {
}
