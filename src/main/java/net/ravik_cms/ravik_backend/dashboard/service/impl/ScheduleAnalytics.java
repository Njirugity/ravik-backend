package net.ravik_cms.ravik_backend.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarRepository;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.dashboard.dto.ScheduleDashboardDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.ScheduleService;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Builds the schedule section of the dashboard from the project's CPM/forecast fields.
 * <p>
 * Variances use the project calendar's working days so they line up with the forecast
 * engine; projects without a calendar fall back to calendar days.
 */
@Component
@RequiredArgsConstructor
class ScheduleAnalytics {
    // matches VarianceStatus.AT_RISK in the forecast engine
    private static final long NEAR_CRITICAL_FLOAT_DAYS = 3;

    private final CalendarRepository calendarRepository;
    private final CalendarService calendarService;
    private final ScheduleService scheduleService;

    ScheduleDashboardDto build(Projects project, List<Milestones> milestones) {
        UUID projectId = project.getId();
        Calendar calendar = calendarRepository.findByProjectId(projectId).orElse(null);
        LocalDate today = LocalDate.now();
        LocalDate finish = project.getForecastEnd() != null ? project.getForecastEnd() : project.getPlannedEnd();

        return ScheduleDashboardDto.builder()
                .scheduled(project.isScheduled())
                .plannedStart(project.getPlannedStart())
                .plannedEnd(project.getPlannedEnd())
                .forecastEnd(project.getForecastEnd())
                .contractFinish(project.getContractFinish())
                .dataDate(project.getDataDate())
                .scheduleVarianceDays(variance(project.getPlannedEnd(), project.getForecastEnd(), calendar))
                .contractVarianceDays(variance(project.getContractFinish(), project.getForecastEnd(), calendar))
                .percentComplete(percentComplete(milestones))
                .percentTimeElapsed(percentTimeElapsed(project.getPlannedStart(), project.getPlannedEnd(), today))
                .workingDaysRemaining(finish == null ? null : Math.max(0, variance(today, finish, calendar)))
                .usesWorkingCalendar(calendar != null)
                .totalMilestones(milestones.size())
                .criticalCount((int) milestones.stream().filter(Milestones::isCritical).count())
                .forecastCriticalCount((int) milestones.stream().filter(Milestones::isForecastCritical).count())
                .nearCriticalCount((int) milestones.stream()
                        .filter(m -> m.getForecastFloat() != null && m.getForecastFloat() > 0 && m.getForecastFloat() <= NEAR_CRITICAL_FLOAT_DAYS)
                        .count())
                .negativeFloatCount((int) milestones.stream()
                        .filter(m -> m.getForecastFloat() != null && m.getForecastFloat() < 0)
                        .count())
                .criticalPath(project.isScheduled() ? scheduleService.getScheduleSummary(projectId).getCriticalPath() : List.of())
                .build();
    }

    /**
     * Project-level variance shown on the overview; null until both dates exist.
     */
    Long scheduleVarianceDays(Projects project) {
        Calendar calendar = calendarRepository.findByProjectId(project.getId()).orElse(null);
        return variance(project.getPlannedEnd(), project.getForecastEnd(), calendar);
    }

    /**
     * Duration-weighted completion: completed milestones count in full, in-progress ones by
     * the share of their duration already worked off. Null when there is nothing to weigh.
     */
    Double percentComplete(List<Milestones> milestones) {
        long totalDuration = milestones.stream().mapToLong(Milestones::getDuration).sum();
        if (totalDuration == 0) {
            return null;
        }
        double done = 0;
        for (Milestones m : milestones) {
            if (m.getStatus() == ProgressStatus.COMPLETED) {
                done += m.getDuration();
            } else if (m.getStatus() == ProgressStatus.IN_PROGRESS && m.getRemainingDuration() != null) {
                done += Math.clamp(m.getDuration() - m.getRemainingDuration(), 0, m.getDuration());
            }
        }
        return done / totalDuration;
    }

    private Long variance(LocalDate from, LocalDate to, Calendar calendar) {
        if (from == null || to == null) {
            return null;
        }
        return calendar != null ? calendarService.workingDaysBetween(from, to, calendar) : ChronoUnit.DAYS.between(from, to);
    }

    private Double percentTimeElapsed(LocalDate start, LocalDate end, LocalDate today) {
        if (start == null || end == null || !end.isAfter(start)) {
            return null;
        }
        double elapsed = ChronoUnit.DAYS.between(start, today);
        return Math.clamp(elapsed / ChronoUnit.DAYS.between(start, end), 0.0, 1.0);
    }
}
