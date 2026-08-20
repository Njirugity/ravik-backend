package net.ravik_cms.ravik_backend.subContractorPayout.dtos;

import java.time.LocalDate;

public record SubContractorPayoutInfoProjection(
        Long id,
        Long subContractorRequiredId,
        Long subContractorId,
        String subContractorTitle,
        Double actualJobCost,
        Double paidAmount,
        LocalDate jobDate,
        String milestoneTitle) {
}
