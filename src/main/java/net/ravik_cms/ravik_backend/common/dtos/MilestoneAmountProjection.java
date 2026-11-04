package net.ravik_cms.ravik_backend.common.dtos;

import java.util.UUID;

/**
 * JPQL constructor-expression projection: an amount summed per milestone.
 */
public record MilestoneAmountProjection(UUID milestoneId, Double total) {
}
