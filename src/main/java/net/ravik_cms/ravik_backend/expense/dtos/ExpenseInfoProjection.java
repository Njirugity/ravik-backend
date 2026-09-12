package net.ravik_cms.ravik_backend.expense.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ExpenseInfoProjection(
        UUID id,
        String title,
        String description,
        String referenceCode,
        Double amount,
        Double paidAmount,
        PaymentStatus paymentStatus,
        LocalDate date,
        Long categoryId,
        String categoryTitle) {
}
