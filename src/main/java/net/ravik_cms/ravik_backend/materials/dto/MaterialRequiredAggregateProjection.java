package net.ravik_cms.ravik_backend.materials.dto;

import java.util.UUID;

/**
 * JPQL constructor-expression projection: total required quantity/cost for one material type,
 * summed across every milestone in a project.
 */
public record MaterialRequiredAggregateProjection(
        UUID materialListId,
        Double totalQuantityRequired,
        Double totalCostRequired
) {
}
