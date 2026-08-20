package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateMaterialRequiredDto(
        UUID materialListId,
        UUID milestoneId,
        Double quantityRequired,
        Double unitPrice
) {
}
