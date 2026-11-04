package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record EquipmentSubContractorPayoutReportDto(
        List<EquipmentPayoutComparisonLineDto> equipment,
        List<SubContractorPayoutComparisonLineDto> subContractors,
        double totalPlannedEquipmentFuelCost,
        double totalActualEquipmentCost,
        double totalEquipmentPaid,
        double totalPlannedSubContractorAmount,
        double totalActualSubContractorCost,
        double totalSubContractorPaid
) {
}
