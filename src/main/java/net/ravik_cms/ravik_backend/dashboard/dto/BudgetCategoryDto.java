package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

/**
 * Approved budget, milestone-allocated budget and actual cost for one category; {@code category} is null on the totals row.
 */
@Builder
public record BudgetCategoryDto(
        BudgetCategory category,
        double approvedBudget,
        double allocatedBudget,
        double actualCost,
        double remaining,
        Double utilisationPct
) {
}
