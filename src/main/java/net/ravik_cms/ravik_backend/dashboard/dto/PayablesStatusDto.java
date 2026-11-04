package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

/**
 * Payables (payouts, expenses, material deliveries) grouped by payment status.
 */
@Builder
public record PayablesStatusDto(
        PaymentStatus status,
        long count,
        double totalCost,
        double paid,
        double outstanding
) {
}
