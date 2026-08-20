package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MaterialStockDto(
        UUID materialListId,
        String materialName,
        String materialMetric,
        Double totalDelivered,
        Double totalUsed,
        Double remaining
) {
}
