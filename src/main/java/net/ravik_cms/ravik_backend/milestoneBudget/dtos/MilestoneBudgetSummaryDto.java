package net.ravik_cms.ravik_backend.milestoneBudget.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneBudgetSummaryDto {
    private UUID milestoneId;
    private Double totalBudget;
    private Double totalSpent;
    private Double totalVariance;
    private List<BudgetCategorySummaryDto> categories;
}
