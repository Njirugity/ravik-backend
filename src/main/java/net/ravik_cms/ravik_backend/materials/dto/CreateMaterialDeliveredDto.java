package net.ravik_cms.ravik_backend.materials.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CreateMaterialDeliveredDto(
        @NotNull(message = "Material is required")
        UUID materialListId,
        @NotNull(message = "Quantity delivered is required")
        @Positive(message = "Quantity delivered must be greater than zero")
        Double quantityDelivered,
        @NotNull(message = "Unit price is required")
        @PositiveOrZero(message = "Unit price cannot be negative")
        Double unitPrice,
        LocalDate dateDelivered
) {
}
