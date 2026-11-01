package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

/**
 * A milestone with its due date; {@code daysFromToday} is negative when overdue.
 */
@Builder
public record DueMilestoneDto(
        UUID id,
        String title,
        ProgressStatus status,
        LocalDate dueDate,
        long daysFromToday,
        boolean critical
) {
}
