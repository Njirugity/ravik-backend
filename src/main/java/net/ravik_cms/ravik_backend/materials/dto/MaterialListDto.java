package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MaterialListDto(
        UUID id,
        String name,
        String metric,
        UUID projectId,
        Long createdAt,
        Long updatedAt
) {
}
