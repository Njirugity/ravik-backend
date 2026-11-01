package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

import java.util.List;

/**
 * Budget vs actual per category, plus the milestones overrunning their budget the most.
 */
@Builder
public record BudgetDashboardDto(
        List<BudgetCategoryDto> categories,
        BudgetCategoryDto totals,
        List<MilestoneOverrunDto> topMilestoneOverruns
) {
}
