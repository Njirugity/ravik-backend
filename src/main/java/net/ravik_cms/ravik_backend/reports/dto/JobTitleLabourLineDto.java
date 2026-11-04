package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

@Builder
public record JobTitleLabourLineDto(
        Long jobTitleId,
        String jobTitleTitle,
        long headcount,
        long daysPresent,
        long daysAbsent,
        double totalGrossPay
) {
}
