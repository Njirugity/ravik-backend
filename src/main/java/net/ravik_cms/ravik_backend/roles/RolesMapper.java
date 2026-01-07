package net.ravik_cms.ravik_backend.roles;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RolesMapper {
    CreateRoleDto toCreateRoleDto(Roles role);
    Roles fromCreateRole(CreateRoleDto roleDto);
    RoleInfoDto toRoleDto(Roles role);
    List<RoleInfoDto> toRoleDtoList(List<Roles> roles);
    Roles fromRoleDto(RoleInfoDto roleDto);
}
