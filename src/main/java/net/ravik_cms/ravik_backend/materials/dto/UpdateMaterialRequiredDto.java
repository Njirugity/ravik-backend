package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

@Builder
public record UpdateMaterialRequiredDto(
        Double quantityRequired,
        Double unitPrice
) {
}
