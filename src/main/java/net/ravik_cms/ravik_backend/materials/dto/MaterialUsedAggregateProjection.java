package net.ravik_cms.ravik_backend.materials.dto;

import java.util.UUID;

/**
 * JPQL constructor-expression projection: total used quantity for one material type, summed
 * across every usage record in a project.
 */
public record MaterialUsedAggregateProjection(
        UUID materialListId,
        Double totalQuantityUsed
) {
}
