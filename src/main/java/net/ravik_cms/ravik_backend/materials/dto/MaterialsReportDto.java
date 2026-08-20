package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.List;

/**
 * The materials page: every material type in the project with its planned/actual figures, plus
 * project-wide totals for the headline overspend/underspend figure.
 */
@Builder
public record MaterialsReportDto(
        List<MaterialSummaryDto> materials,
        Double totalRequiredCost,
        Double totalDeliveredCost,
        Double totalCostVariance
) {
}
