package net.ravik_cms.ravik_backend.milestones.dtos;

import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

/**
 * JPQL constructor-expression projection: number of milestones in a status.
 */
public record MilestoneStatusCountProjection(ProgressStatus status, Long count) {
}
