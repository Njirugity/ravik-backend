package net.ravik_cms.ravik_backend.jobTitles;

import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

public record JobTitlesInfoProjection(
        Long id,
        String title,
        Double baseWage,
        PaymentFrequency frequency,
        Long memberships) {
}
