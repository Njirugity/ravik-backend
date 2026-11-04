package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

/**
 * Actual cost vs amount paid for one budget category.
 */
@Builder
public record CategoryPaymentDto(
        BudgetCategory category,
        double actualCost,
        double paid,
        double outstanding,
        Double paymentProgressPct
) {
}
