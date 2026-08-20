package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record MaterialUsedDto(
        UUID id,
        UUID materialDeliveredId,
        UUID materialListId,
        String materialName,
        String materialMetric,
        UUID milestoneId,
        UUID projectId,
        Double quantityUsed,
        LocalDate dateUsed,
        UUID createdByUserId,
        String createdByUserName,
        Long createdAt,
        Long updatedAt
) {
}
