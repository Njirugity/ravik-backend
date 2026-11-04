package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

@Builder
public record SubContractorPayoutComparisonLineDto(
        Long subContractorRequiredId,
        Long subContractorId,
        String subContractorTitle,
        double plannedAmount,
        double actualCost,
        double paidAmount,
        double costVariance
) {
}
