package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.UUID;

/**
 * One row of the materials page: a material type's planned vs actual quantities/costs across
 * the whole project, plus the variances that flag over/underspend and over/underuse.
 */
@Builder
public record MaterialSummaryDto(
        UUID materialListId,
        String materialName,
        String materialMetric,
        Double requiredQuantity,
        Double requiredCost,
        Double deliveredQuantity,
        Double deliveredCost,
        Double usedQuantity,
        Double balance,
        Double quantityVariance,
        Double costVariance
) {
}
