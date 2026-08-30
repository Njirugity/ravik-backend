package net.ravik_cms.ravik_backend.costSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostCategoryLineDto {
    private BudgetCategory category;
    private Double totalActualCost;
    private Double totalPaid;
    private Double outstanding;
    private Double paymentProgressPct;
}
