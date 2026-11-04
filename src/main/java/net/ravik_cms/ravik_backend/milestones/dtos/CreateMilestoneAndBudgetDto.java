package net.ravik_cms.ravik_backend.milestones.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.CreateMilestoneBudgetLineDto;

import java.util.List;

public record CreateMilestoneAndBudgetDto(
        @NotNull(message = "Milestone details are required") @Valid CreateMilestoneDto milestoneDto,
        List<CreateMilestoneBudgetLineDto> budgetDtoList
) {
}
