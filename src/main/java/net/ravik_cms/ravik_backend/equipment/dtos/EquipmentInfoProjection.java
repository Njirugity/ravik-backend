package net.ravik_cms.ravik_backend.equipment.dtos;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;

public record EquipmentInfoProjection(
        Long id,
        String title,
        EquipmentCategory category,
        Long capacity,
        String metric
        ) {
}
