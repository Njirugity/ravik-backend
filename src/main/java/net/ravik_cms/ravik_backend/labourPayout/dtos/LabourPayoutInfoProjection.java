package net.ravik_cms.ravik_backend.labourPayout.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record LabourPayoutInfoProjection(
        UUID id,
        LocalDate periodStart,
        LocalDate periodEnd,
        Double totalAmount,
        String referenceCode,
        Double paidAmount,
        PaymentStatus paymentStatus) {
}
