package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record LabourAttendanceCostReportDto(
        List<JobTitleLabourLineDto> byJobTitle,
        long totalHeadcount,
        long totalDaysPresent,
        long totalDaysAbsent,
        double totalGrossPay,
        double totalLabourPayoutAmount,
        double totalLabourPaid,
        double totalLabourOutstanding
) {
}
