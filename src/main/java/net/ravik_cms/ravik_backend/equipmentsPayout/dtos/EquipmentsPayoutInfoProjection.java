package net.ravik_cms.ravik_backend.equipmentsPayout.dtos;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record EquipmentsPayoutInfoProjection(
        UUID id,
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
        String referenceCode,
        long totalCost,
        String milestoneTitle,
        Double paidAmount,
        PaymentStatus paymentStatus) {
}
