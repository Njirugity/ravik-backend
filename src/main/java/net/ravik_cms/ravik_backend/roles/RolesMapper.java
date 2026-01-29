package net.ravik_cms.ravik_backend.roles;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolesMapper {
    CreateRoleDto toCreateRoleDto(Roles role);
    Roles fromCreateRole(CreateRoleDto roleDto);
    RoleInfoDto toRoleDto(Roles role);
    List<RoleInfoDto> toRoleDtoList(List<Roles> roles);
    Roles fromRoleDto(RoleInfoDto roleDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(RoleInfoDto dto, @MappingTarget Roles roles);
}
