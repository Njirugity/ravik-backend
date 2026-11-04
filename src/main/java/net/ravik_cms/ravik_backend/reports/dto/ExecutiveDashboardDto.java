package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ExecutiveDashboardDto(
        UUID projectId,

        Double approvedBudget,
        double totalActualCost,
        Double budgetUtilizationPct,
        double budgetVariance,

        double totalIncome,
        double totalPayments,
        double netCashPosition,
        double fundingGap,

        boolean scheduled,
        LocalDate scheduleStartDate,
        LocalDate scheduleEndDate,
        Long criticalPathDuration,
        Integer criticalMilestoneCount,
        Integer totalMilestoneCount,

        long milestonesCompleted,
        long milestonesInProgress,
        long milestonesPending,
        long milestonesOverdue,

        double totalOutstandingPayables,
        long outstandingPayablesCount,

        long approvalsExpiringSoon,
        long approvalsExpired,

        double totalLabourOutstanding
) {
}
