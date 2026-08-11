package net.ravik_cms.ravik_backend.roles;

import net.ravik_cms.ravik_backend.permission.PermissionsMapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", uses = PermissionsMapper.class)
public interface RolesMapper {
    CreateRoleDto toCreateRoleDto(Roles role);
    Roles fromCreateRole(CreateRoleDto roleDto);
    RoleInfoDto toRoleDto(Roles role);
    List<RoleInfoDto> toRoleDtoList(List<Roles> roles);
    Roles fromRoleDto(RoleInfoDto roleDto);
    @Mapping(target = "permissions", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(RoleInfoDto dto, @MappingTarget Roles roles);
}
