package net.ravik_cms.ravik_backend.common.dtos;

import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

public record CategoryAmountProjection(BudgetCategory category, Double total) {
}
