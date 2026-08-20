package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record MaterialDeliveredDto(
        UUID id,
        UUID materialListId,
        String materialName,
        String materialMetric,
        UUID projectId,
        Double quantityDelivered,
        Double unitPrice,
        Double totalCost,
        LocalDate dateDelivered,
        UUID createdByUserId,
        String createdByUserName,
        Long createdAt,
        Long updatedAt
) {
}
