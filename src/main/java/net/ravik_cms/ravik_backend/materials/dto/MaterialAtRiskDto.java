package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

/**
 * One material shortfall on a milestone that's due within the alert threshold: the milestone
 * needs more of this material than is currently in store (delivered minus used, project-wide).
 */
@Builder
public record MaterialAtRiskDto(
        UUID milestoneId,
        String milestoneTitle,
        LocalDate milestoneDueDate,
        UUID materialListId,
        String materialName,
        String materialMetric,
        Double requiredQuantity,
        Double inStoreQuantity,
        Double shortfallQuantity
) {
}
