package net.ravik_cms.ravik_backend.dashboard.dto;

import lombok.Builder;

import java.util.UUID;

/**
 * A milestone whose actual cost (equipment, subcontractor and wages) exceeds its budget.
 */
@Builder
public record MilestoneOverrunDto(
        UUID milestoneId,
        String title,
        double budget,
        double actualCost,
        double overrun
) {
}
