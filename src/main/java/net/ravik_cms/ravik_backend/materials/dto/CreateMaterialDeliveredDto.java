package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CreateMaterialDeliveredDto(
        UUID materialListId,
        Double quantityDelivered,
        Double unitPrice,
        LocalDate dateDelivered
) {
}
