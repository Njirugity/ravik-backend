package net.ravik_cms.ravik_backend.budget.mapper;

import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;
import net.ravik_cms.ravik_backend.budget.dtos.UpdateBudgetDto;
import net.ravik_cms.ravik_backend.budget.entity.Budget;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    Budget toEntity(CreateBudgetDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBudget(UpdateBudgetDto dto, @MappingTarget Budget entity);
}
