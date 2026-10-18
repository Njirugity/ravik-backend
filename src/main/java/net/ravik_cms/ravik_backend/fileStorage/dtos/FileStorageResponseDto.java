package net.ravik_cms.ravik_backend.fileStorage.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileStorageResponseDto(
        UUID id,
        String fileName,
        String contentType,
        long fileSize,
        UUID milestoneId,
        UUID projectId,
        LocalDateTime uploadedAt
) {
}
