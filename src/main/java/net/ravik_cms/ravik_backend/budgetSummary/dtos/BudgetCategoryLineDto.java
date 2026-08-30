package net.ravik_cms.ravik_backend.budgetSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCategoryLineDto {
    private BudgetCategory category;
    private Double approvedBudget;
    private Double rolledUpBudget;
    private Double milestoneCost;
    private Double unscopedExpense;
    private Double totalActualCost;
    private Double planningVariance;
    private Double headlineVariance;
}
