package net.ravik_cms.ravik_backend.permission;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionsMapper {
    PermissionInfoDto toPermissionDto(Permissions permission);
    Permissions toPermissions(PermissionInfoDto permissionDto);
    List<PermissionInfoDto> toPermissionsListDto(List<Permissions> permissionsList);
}
