package net.ravik_cms.ravik_backend.subContractorPayout.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record SubContractorPayoutInfoProjection(
        UUID id,
        Long subContractorRequiredId,
        Long subContractorId,
        String subContractorTitle,
        Double actualJobCost,
        String referenceCode,
        Double paidAmount,
        LocalDate jobDate,
        String milestoneTitle,
        PaymentStatus paymentStatus) {
}
