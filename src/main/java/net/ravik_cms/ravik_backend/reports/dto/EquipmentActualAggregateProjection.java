package net.ravik_cms.ravik_backend.reports.dto;

public record EquipmentActualAggregateProjection(
        Long equipmentRequiredId,
        Long totalWorkedDuration,
        Double totalActualCost,
        Double totalPaidAmount
) {
}
