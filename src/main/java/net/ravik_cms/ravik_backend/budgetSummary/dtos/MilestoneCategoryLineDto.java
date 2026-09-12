package net.ravik_cms.ravik_backend.budgetSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneCategoryLineDto {
    private BudgetCategory category;
    private Double budget;
    private Double actualCost;
    private Double variance;
}
