package net.ravik_cms.ravik_backend.expenseCategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateExpenseCategoryDto {
    private String title;
    private BudgetCategory budgetCategory;
}
