package net.ravik_cms.ravik_backend.expense.dtos;

import java.util.UUID;

public record ExpenseInfoProjection(
        UUID id,
        String title,
        String description,
        Double amount) {
}
