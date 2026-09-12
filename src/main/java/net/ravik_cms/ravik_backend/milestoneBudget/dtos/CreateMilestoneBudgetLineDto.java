package net.ravik_cms.ravik_backend.milestoneBudget.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMilestoneBudgetLineDto {
    private BudgetCategory category;
    private Double amount;
}
