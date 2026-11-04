package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

import java.util.List;

/**
 * Cash position, payment progress per category, payables by status and the monthly cash-flow trend.
 */
@Builder
public record PaymentsDashboardDto(
        double totalIncome,
        double totalPaid,
        double netCashPosition,
        double totalActualCost,
        double outstanding,
        List<CategoryPaymentDto> byCategory,
        List<PayablesStatusDto> payablesByStatus,
        List<MonthlyCashFlowDto> monthlyTrend
) {
}
