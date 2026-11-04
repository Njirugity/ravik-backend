package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;

@Builder
public record PayableCategoryLineDto(
        PaymentCategory category,
        long count,
        double totalOutstanding
) {
}
