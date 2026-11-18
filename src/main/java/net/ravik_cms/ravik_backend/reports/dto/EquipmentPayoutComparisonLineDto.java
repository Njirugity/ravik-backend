package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;

@Builder
public record EquipmentPayoutComparisonLineDto(
        Long equipmentRequiredId,
        Long equipmentId,
        String equipmentTitle,
        EquipmentCategory equipmentCategory,
        long plannedDuration,
        double plannedFuelCost,
        double plannedCost,
        long actualDuration,
        double actualCost,
        double paidAmount,
        double costVariance
) {
}
