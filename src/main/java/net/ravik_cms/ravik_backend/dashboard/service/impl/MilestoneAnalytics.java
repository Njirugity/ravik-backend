package net.ravik_cms.ravik_backend.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.dashboard.dto.DueMilestoneDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MilestoneDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PhaseProgressDto;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneStatusCountProjection;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.milestones.service.DateEvaluator;
import net.ravik_cms.ravik_backend.phase.entity.Phases;
import net.ravik_cms.ravik_backend.phase.repository.PhasesRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Builds the milestones section of the dashboard.
 * <p>
 * "Overdue" and "upcoming" use {@code earliestFinish}, matching the existing milestone summary
 * and material alerts; {@code dateStatusCounts} uses {@link DateEvaluator}'s latest-date view.
 */
@Component
@RequiredArgsConstructor
class MilestoneAnalytics {
    private static final int LIST_LIMIT = 10;

    private final MilestonesRepository milestonesRepository;
    private final PhasesRepository phasesRepository;
    private final DateEvaluator dateEvaluator;

    MilestoneDashboardDto build(UUID projectId, List<Milestones> milestones, int upcomingDays) {
        LocalDate today = LocalDate.now();

        Map<ProgressStatus, Long> statusCounts = new EnumMap<>(ProgressStatus.class);
        for (ProgressStatus status : ProgressStatus.values()) {
            statusCounts.put(status, 0L);
        }
        for (MilestoneStatusCountProjection row : milestonesRepository.countByProjectGroupByStatus(projectId)) {
            if (row.status() != null) {
                statusCounts.put(row.status(), row.count());
            }
        }

        Map<DateStatus, Long> dateStatusCounts = new EnumMap<>(DateStatus.class);
        for (DateStatus status : DateStatus.values()) {
            dateStatusCounts.put(status, 0L);
        }
        milestones.forEach(m -> dateStatusCounts.merge(dateEvaluator.calculateDateStatus(m), 1L, Long::sum));

        List<Milestones> overdue = milestonesRepository.findOverdueMilestones(projectId, today);
        List<Milestones> upcoming = milestonesRepository.findAlmostDueMilestones(projectId, today, today.plusDays(upcomingDays));

        return MilestoneDashboardDto.builder()
                .totalMilestones(milestones.size())
                .statusCounts(statusCounts)
                .dateStatusCounts(dateStatusCounts)
                .overdueCount(overdue.size())
                .overdue(overdue.stream()
                        .sorted(Comparator.comparing(Milestones::getEarliestFinish))
                        .limit(LIST_LIMIT)
                        .map(m -> toDue(m, today))
                        .toList())
                .upcoming(upcoming.stream()
                        .sorted(Comparator.comparing(Milestones::getEarliestFinish))
                        .limit(LIST_LIMIT)
                        .map(m -> toDue(m, today))
                        .toList())
                .phases(phaseProgress(projectId, milestones))
                .build();
    }

    private List<PhaseProgressDto> phaseProgress(UUID projectId, List<Milestones> milestones) {
        Map<UUID, List<Milestones>> byPhase = milestones.stream()
                .filter(m -> m.getPhase() != null)
                .collect(Collectors.groupingBy(m -> m.getPhase().getId()));

        return phasesRepository.findAllByProjectIdOrderByPlannedStartDateAsc(projectId).stream()
                .map(phase -> toPhaseProgress(phase, byPhase.getOrDefault(phase.getId(), List.of())))
                .toList();
    }

    private PhaseProgressDto toPhaseProgress(Phases phase, List<Milestones> milestones) {
        long completed = milestones.stream().filter(m -> m.getStatus() == ProgressStatus.COMPLETED).count();
        return PhaseProgressDto.builder()
                .id(phase.getId())
                .title(phase.getTitle())
                .status(phase.getStatus())
                .plannedStart(phase.getPlannedStartDate())
                .plannedEnd(phase.getPlannedEndDate())
                .actualEnd(phase.getActualEndDate())
                .totalMilestones(milestones.size())
                .completedMilestones(completed)
                .percentComplete(milestones.isEmpty() ? null : (double) completed / milestones.size())
                .build();
    }

    private DueMilestoneDto toDue(Milestones m, LocalDate today) {
        return DueMilestoneDto.builder()
                .id(m.getId())
                .title(m.getTitle())
                .status(m.getStatus())
                .dueDate(m.getEarliestFinish())
                .daysFromToday(ChronoUnit.DAYS.between(today, m.getEarliestFinish()))
                .critical(m.isCritical())
                .build();
    }
}
