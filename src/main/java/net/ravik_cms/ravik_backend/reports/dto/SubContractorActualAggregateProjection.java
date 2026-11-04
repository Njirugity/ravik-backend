package net.ravik_cms.ravik_backend.reports.dto;

public record SubContractorActualAggregateProjection(
        Long subContractorRequiredId,
        Double totalActualCost,
        Double totalPaidAmount
) {
}
