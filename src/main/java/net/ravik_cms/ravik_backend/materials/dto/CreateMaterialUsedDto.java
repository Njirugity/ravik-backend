package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CreateMaterialUsedDto(
        UUID materialDeliveredId,
        UUID milestoneId,
        Double quantityUsed,
        LocalDate dateUsed
) {
}
