package net.ravik_cms.ravik_backend.account.mapper;

import net.ravik_cms.ravik_backend.account.dtos.CreateAccountDto;
import net.ravik_cms.ravik_backend.account.dtos.UpdateAccountDto;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Accounts toEntity(CreateAccountDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAccount(UpdateAccountDto dto, @MappingTarget Accounts entity);
}
