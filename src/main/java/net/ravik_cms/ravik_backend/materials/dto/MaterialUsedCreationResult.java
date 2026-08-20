package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

/**
 * What {@code create()} hands back to the controller: the persisted usage record plus whether
 * this usage pushed the material's total used past what was actually delivered. The stock check
 * is a soft warning, not a hard block — site reality (undocumented/late-recorded deliveries)
 * means we still save the entry either way.
 */
@Builder
public record MaterialUsedCreationResult(
        MaterialUsedDto materialUsed,
        boolean stockExceeded,
        Double remainingStock
) {
}
