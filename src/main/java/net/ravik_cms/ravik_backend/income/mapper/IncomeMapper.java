package net.ravik_cms.ravik_backend.income.mapper;

import net.ravik_cms.ravik_backend.income.dtos.CreateIncomeDto;
import net.ravik_cms.ravik_backend.income.dtos.UpdateIncomeDto;
import net.ravik_cms.ravik_backend.income.entity.Income;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IncomeMapper {
    Income toEntity(CreateIncomeDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateIncome(UpdateIncomeDto dto, @MappingTarget Income entity);
}
