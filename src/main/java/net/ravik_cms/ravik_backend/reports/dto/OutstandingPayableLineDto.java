package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record OutstandingPayableLineDto(
        UUID id,
        PaymentCategory paymentCategory,
        String referenceCode,
        String title,
        Double totalCost,
        Double paidAmount,
        Double outstandingAmount,
        PaymentStatus paymentStatus,
        LocalDate date
) {
}
