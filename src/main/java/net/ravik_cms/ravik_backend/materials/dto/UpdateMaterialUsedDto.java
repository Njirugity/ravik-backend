package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UpdateMaterialUsedDto(
        Double quantityUsed,
        LocalDate dateUsed
) {
}
