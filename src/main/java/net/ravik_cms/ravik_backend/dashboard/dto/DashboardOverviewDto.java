package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Headline KPI tiles for a project: progress, schedule, money and risk at a glance.
 */
@Builder
public record DashboardOverviewDto(
        UUID projectId,
        String title,
        ProgressStatus progress,
        Double percentComplete,
        LocalDate plannedEnd,
        LocalDate forecastEnd,
        Long scheduleVarianceDays,
        double totalBudget,
        double totalActualCost,
        Double budgetUsedPct,
        double totalPaid,
        double outstanding,
        double totalIncome,
        double netCashPosition,
        long totalMilestones,
        long completedMilestones,
        long overdueMilestones,
        int materialsAtRisk
) {
}
