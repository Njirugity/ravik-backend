package net.ravik_cms.ravik_backend.permission;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionsMapper {
    PermissionInfoDto toPermissionDto(Permissions permission);
    Permissions toPermissions(PermissionInfoDto permissionDto);
}
