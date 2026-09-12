package net.ravik_cms.ravik_backend.expense.mapper;

import net.ravik_cms.ravik_backend.expense.dtos.CreateExpenseDto;
import net.ravik_cms.ravik_backend.expense.dtos.UpdateExpenseDto;
import net.ravik_cms.ravik_backend.expense.entity.Expenses;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    Expenses toEntity(CreateExpenseDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateExpense(UpdateExpenseDto dto, @MappingTarget Expenses entity);
}
