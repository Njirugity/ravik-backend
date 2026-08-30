package net.ravik_cms.ravik_backend.budget.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBudgetDto {
    private Double amount;
    private BudgetCategory category;
}
