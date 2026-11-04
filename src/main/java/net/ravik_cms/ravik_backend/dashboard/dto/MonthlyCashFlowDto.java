package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

/**
 * Money in and out of the project in one calendar month ({@code month} as yyyy-MM).
 */
@Builder
public record MonthlyCashFlowDto(
        String month,
        double income,
        double paid,
        double net
) {
}
