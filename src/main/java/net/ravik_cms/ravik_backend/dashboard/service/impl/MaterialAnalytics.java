package net.ravik_cms.ravik_backend.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.dashboard.dto.MaterialsDashboardDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialAtRiskDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialSummaryDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialsReportDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialAlertService;
import net.ravik_cms.ravik_backend.materials.service.MaterialsReportService;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Builds the materials section of the dashboard on top of the materials report and alerts,
 * using their unauthorised build methods since the dashboard checks READ_DASHBOARD itself.
 */
@Component
@RequiredArgsConstructor
class MaterialAnalytics {
    static final int AT_RISK_DAYS = 7;
    private static final int LIST_LIMIT = 10;
    private static final int CHART_LIMIT = 8;

    private final MaterialsReportService materialsReportService;
    private final MaterialAlertService materialAlertService;

    MaterialsDashboardDto build(UUID projectId) {
        MaterialsReportDto report = materialsReportService.getProjectReport(projectId);
        List<MaterialAtRiskDto> atRisk = materialAlertService.getAtRiskMaterials(projectId, AT_RISK_DAYS);

        return MaterialsDashboardDto.builder()
                .materialCount(report.materials().size())
                .totalRequiredCost(nz(report.totalRequiredCost()))
                .totalDeliveredCost(nz(report.totalDeliveredCost()))
                .totalCostVariance(nz(report.totalCostVariance()))
                .stockShortfalls(report.materials().stream()
                        .filter(m -> shortfall(m) > 0)
                        .sorted(Comparator.comparingDouble(MaterialAnalytics::shortfall).reversed())
                        .limit(LIST_LIMIT)
                        .toList())
                .atRiskCount(atRisk.size())
                .atRisk(atRisk.stream().limit(LIST_LIMIT).toList())
                // Ranked by planned cost: costs share a unit (KES) where quantities do not.
                .topByCost(report.materials().stream()
                        .sorted(Comparator.comparingDouble((MaterialSummaryDto m) -> nz(m.requiredCost())).reversed())
                        .limit(CHART_LIMIT)
                        .toList())
                .build();
    }

    int atRiskCount(UUID projectId) {
        return materialAlertService.getAtRiskMaterials(projectId, AT_RISK_DAYS).size();
    }

    /**
     * How much more is still needed than is in store: (required − used) − balance.
     */
    private static double shortfall(MaterialSummaryDto m) {
        double stillNeeded = nz(m.requiredQuantity()) - nz(m.usedQuantity());
        return stillNeeded - nz(m.balance());
    }

    private static double nz(Double value) {
        return value != null ? value : 0.0;
    }
}
