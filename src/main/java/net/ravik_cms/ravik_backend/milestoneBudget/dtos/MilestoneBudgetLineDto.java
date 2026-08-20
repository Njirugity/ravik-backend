package net.ravik_cms.ravik_backend.milestoneBudget.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneBudgetLineDto {
    private Long id;
    private UUID milestoneId;
    private BudgetCategory category;
    private Double amount;
}
