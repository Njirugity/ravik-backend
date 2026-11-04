package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record OutstandingPayablesReportDto(
        List<OutstandingPayableLineDto> payables,
        List<PayableCategoryLineDto> byCategory,
        long totalCount,
        double totalOutstandingAmount
) {
}
