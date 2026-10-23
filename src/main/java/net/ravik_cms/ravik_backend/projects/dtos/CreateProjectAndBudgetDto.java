package net.ravik_cms.ravik_backend.projects.dtos;

import jakarta.validation.Valid;
import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;

import java.util.List;

public record CreateProjectAndBudgetDto(
        @Valid ProjectDto projectDto,
        List<CreateBudgetDto> budgetDtoList
) {
}
