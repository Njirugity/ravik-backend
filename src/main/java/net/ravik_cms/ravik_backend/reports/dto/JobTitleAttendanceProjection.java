package net.ravik_cms.ravik_backend.reports.dto;

public record JobTitleAttendanceProjection(
        Long jobTitleId,
        String jobTitleTitle,
        Long headcount,
        Long daysPresent,
        Long daysAbsent
) {
}
