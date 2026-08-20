package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

@Builder
public record DeliveredStockDto(
        Double totalDelivered
) {
}
