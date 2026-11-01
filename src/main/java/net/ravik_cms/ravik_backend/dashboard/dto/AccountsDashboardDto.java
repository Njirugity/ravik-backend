package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

import java.util.List;

/**
 * Accounts that moved money on this project, with their overall balances.
 */
@Builder
public record AccountsDashboardDto(
        List<AccountDashboardDto> accounts,
        double totalIncome,
        double totalPayments
) {
}
