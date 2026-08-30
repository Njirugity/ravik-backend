package net.ravik_cms.ravik_backend.income.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record IncomeInfoProjection(
        UUID id,
        String source,
        String description,
        Double amount,
        LocalDate date) {
}
