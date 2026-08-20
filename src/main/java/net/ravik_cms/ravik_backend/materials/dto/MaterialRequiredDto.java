package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record MaterialRequiredDto(
        UUID id,
        UUID materialListId,
        String materialName,
        String materialMetric,
        UUID milestoneId,
        UUID projectId,
        Double quantityRequired,
        Double unitPrice,
        Double totalCost,
        UUID createdByUserId,
        String createdByUserName,
        Long createdAt,
        Long updatedAt
) {
}
