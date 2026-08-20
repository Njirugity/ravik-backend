package net.ravik_cms.ravik_backend.equipmentsPayout.dtos;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;

import java.time.LocalDate;

public record EquipmentsPayoutInfoProjection(
        Long id,
        Long equipmentRequiredId,
        Long equipmentId,
        String equipmentTitle,
        EquipmentCategory equipmentCategory,
        long workedDuration,
        long rentalCost,
        long fuelCost,
        long operatorCost,
        LocalDate dateUsed,
        String notes,
        long totalCost,
        String milestoneTitle,
        Double paidAmount) {
}
