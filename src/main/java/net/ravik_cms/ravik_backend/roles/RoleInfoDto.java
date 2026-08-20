package net.ravik_cms.ravik_backend.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.RoleType;
import net.ravik_cms.ravik_backend.permission.PermissionInfoDto;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfoDto {
    private UUID id;
    private String name;
    private RoleType roleType;
    private Set<PermissionInfoDto> permissions;
    private Long memberCount;
}
