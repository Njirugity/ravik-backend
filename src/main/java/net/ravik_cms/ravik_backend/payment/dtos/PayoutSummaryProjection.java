package net.ravik_cms.ravik_backend.payment.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PayoutSummaryProjection(
        Long id,
        PaymentCategory paymentCategory,
        String referenceCode,
        String title,
        UUID projectId,
        Double totalCost,
        Double paidAmount,
        PaymentStatus paymentStatus,
        LocalDate date) {
}
