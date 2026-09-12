package net.ravik_cms.ravik_backend.budget.dtos;

import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.BudgetSource;

import java.util.UUID;

public record BudgetInfoProjection(
        UUID id,
        Double amount,
        BudgetCategory category,
        BudgetSource source) {
}
