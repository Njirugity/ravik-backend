package net.ravik_cms.ravik_backend.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RolesController {
    private final RolesService rolesService;
    @PostMapping("/assign-permission/{roleId}/{permId}")
    public void assignRole(@PathVariable UUID roleId, @RequestParam("permissionKeys") Set<String> permissionKeys) {
        rolesService.setPermissions(roleId, permissionKeys);
    }
    @GetMapping("/{id}")
    public RoleInfoDto getRole(@PathVariable UUID id, @PathVariable UUID project_id){
        return rolesService.getRole(id,  project_id);
    }
    @GetMapping
    public List<RoleInfoDto> getAllRoles(@PathVariable UUID project_id){
        return rolesService.getAllRoles(project_id);
    }
    @PostMapping
    public RoleInfoDto createRole(@RequestBody CreateRoleDto role, @PathVariable UUID project_id){
        return rolesService.createRole(role, project_id);
    }
    @PatchMapping("/edit-role/{roleId}/{projectId}")
    public RoleInfoDto updateRole(@PathVariable UUID roleId, @PathVariable UUID projectId, RoleInfoDto request) {
        return rolesService.updateRole(roleId, projectId, request);
    }
    @DeleteMapping
    public void deleteRole(@PathVariable UUID roleId, @PathVariable UUID project_id){
        rolesService.deleteRole(roleId, project_id);
    }
}
