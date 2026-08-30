package net.ravik_cms.ravik_backend.milestoneBudget.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetCategorySummaryDto {
    private BudgetCategory category;
    private Double budgeted;
    private Double spent;
    private Double variance;
}
