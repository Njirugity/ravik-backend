package net.ravik_cms.ravik_backend.materials.dto;

import java.util.UUID;

/**
 * JPQL constructor-expression projection: total delivered quantity/cost for one material type,
 * summed across every (non-deleted) delivery in a project.
 */
public record MaterialDeliveredAggregateProjection(
        UUID materialListId,
        Double totalQuantityDelivered,
        Double totalCostDelivered
) {
}
