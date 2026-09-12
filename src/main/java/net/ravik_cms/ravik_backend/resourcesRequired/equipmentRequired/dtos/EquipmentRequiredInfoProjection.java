package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;

import java.time.LocalDate;
import java.util.UUID;

public record EquipmentRequiredInfoProjection(
        Long id,
        Long equipmentId,
        String equipmentTitle,
        EquipmentCategory category,
        UUID milestoneId,
        long plannedDuration,
        long plannedFuelCost,
        String notes,
        LocalDate dateRequired) {
}
