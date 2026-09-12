package net.ravik_cms.ravik_backend.costSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CostPaymentSummaryDto {
    private UUID projectId;
    private List<CostCategoryLineDto> categories;
    private CostCategoryLineDto totals;
}
