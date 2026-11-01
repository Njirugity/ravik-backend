package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.materials.dto.MaterialAtRiskDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialSummaryDto;

import java.util.List;

/**
 * Material cost vs plan, stock shortfalls against remaining requirements, at-risk materials for milestones due soon, and the costliest materials for charting.
 */
@Builder
public record MaterialsDashboardDto(
        int materialCount,
        double totalRequiredCost,
        double totalDeliveredCost,
        double totalCostVariance,
        List<MaterialSummaryDto> stockShortfalls,
        int atRiskCount,
        List<MaterialAtRiskDto> atRisk,
        List<MaterialSummaryDto> topByCost
) {
}
