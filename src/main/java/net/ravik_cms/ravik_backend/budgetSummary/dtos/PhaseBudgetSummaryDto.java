package net.ravik_cms.ravik_backend.budgetSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhaseBudgetSummaryDto {
    private UUID phaseId;
    private List<PhaseCategoryLineDto> categories;
    private PhaseCategoryLineDto totals;
}
