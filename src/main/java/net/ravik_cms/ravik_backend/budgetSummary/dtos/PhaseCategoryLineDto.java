package net.ravik_cms.ravik_backend.budgetSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhaseCategoryLineDto {
    private BudgetCategory category;
    private Double rolledUpBudget;
    private Double milestoneCost;
    private Double totalActualCost;
    private Double variance;
}
