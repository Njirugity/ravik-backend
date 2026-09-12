package net.ravik_cms.ravik_backend.expenseCategory.mapper;

import net.ravik_cms.ravik_backend.expenseCategory.dto.CreateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.dto.UpdateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExpenseCategoryMapper {
    ExpenseCategory toEntity(CreateExpenseCategoryDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpenseCategory(UpdateExpenseCategoryDto dto, @MappingTarget ExpenseCategory entity);
}
