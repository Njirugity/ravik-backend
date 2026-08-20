package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateMaterialDeliveredDto(
        Double quantityDelivered,
        Double unitPrice,
        LocalDate dateDelivered
) {
}
