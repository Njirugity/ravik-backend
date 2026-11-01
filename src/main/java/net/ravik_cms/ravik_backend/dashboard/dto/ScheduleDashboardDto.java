package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.CriticalPathMilestoneDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Schedule health: baseline vs forecast dates, variance, progress and critical-path exposure. Variances are in working days (calendar days when the project has no calendar); positive means late.
 */
@Builder
public record ScheduleDashboardDto(
        boolean scheduled,
        LocalDate plannedStart,
        LocalDate plannedEnd,
        LocalDate forecastEnd,
        LocalDate contractFinish,
        LocalDate dataDate,
        Long scheduleVarianceDays,
        Long contractVarianceDays,
        Double percentComplete,
        Double percentTimeElapsed,
        Long workingDaysRemaining,
        boolean usesWorkingCalendar,
        int totalMilestones,
        int criticalCount,
        int forecastCriticalCount,
        int nearCriticalCount,
        int negativeFloatCount,
        List<CriticalPathMilestoneDto> criticalPath
) {
}
