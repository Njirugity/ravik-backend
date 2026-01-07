package net.ravik_cms.ravik_backend.roles;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.permission.PermissionInfoDto;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfoDto {
    private Long id;
    private String name;
    private Set<PermissionInfoDto> permissions;
}
