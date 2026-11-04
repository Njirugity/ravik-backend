package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Progress of one phase, measured by completed milestones.
 */
@Builder
public record PhaseProgressDto(
        UUID id,
        String title,
        ProgressStatus status,
        LocalDate plannedStart,
        LocalDate plannedEnd,
        LocalDate actualEnd,
        long totalMilestones,
        long completedMilestones,
        Double percentComplete
) {
}
