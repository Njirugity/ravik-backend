package net.ravik_cms.ravik_backend.subContractorPayout.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;

public record SubContractorPayoutInfoProjection(
        Long id,
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
