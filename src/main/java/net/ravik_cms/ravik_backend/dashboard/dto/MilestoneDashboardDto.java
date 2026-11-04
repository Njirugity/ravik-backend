package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.util.List;
import java.util.Map;

/**
 * Milestone status breakdowns, overdue and upcoming milestones, and per-phase progress.
 */
@Builder
public record MilestoneDashboardDto(
        long totalMilestones,
        Map<ProgressStatus, Long> statusCounts,
        Map<DateStatus, Long> dateStatusCounts,
        long overdueCount,
        List<DueMilestoneDto> overdue,
        List<DueMilestoneDto> upcoming,
        List<PhaseProgressDto> phases
) {
}
