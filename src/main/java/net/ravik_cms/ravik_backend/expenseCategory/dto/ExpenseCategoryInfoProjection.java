package net.ravik_cms.ravik_backend.expenseCategory.dto;

import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

public record ExpenseCategoryInfoProjection(
        Long id,
        String title,
        BudgetCategory budgetCategory) {
}
