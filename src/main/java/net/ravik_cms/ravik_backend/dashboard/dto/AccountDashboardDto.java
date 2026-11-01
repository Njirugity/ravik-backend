package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

import java.util.UUID;

/**
 * One account's flows on this project; {@code currentBalance} is across all projects and null when the account belongs to another client.
 */
@Builder
public record AccountDashboardDto(
        UUID accountId,
        String name,
        String type,
        double incomeThisProject,
        double paymentsThisProject,
        Double currentBalance
) {
}
