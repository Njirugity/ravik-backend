package net.ravik_cms.ravik_backend.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RolesController {
    private final RolesService rolesService;
    private final AuthorizationService authorizationService;
    @PostMapping("/assign-permission/{roleId}")
    public ResponseEntity<?> assignRole(
            @PathVariable UUID roleId,
            @RequestParam(value = "permissionKeys", required = false) Set<String> permissionKeys) {
        authorizationService.authorize("UPDATE_ROLE");
        rolesService.setPermissions(roleId, permissionKeys == null ? Set.of() : permissionKeys);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{project_id}/{id}")
    public ResponseEntity<RoleInfoDto> getRole(@PathVariable UUID project_id,@PathVariable UUID id){
        authorizationService.authorize("READ_ROLE");
        RoleInfoDto body = rolesService.getRole(id,  project_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/{project_id}")
    public ResponseEntity<List<RoleInfoDto>> getAllRoles(@PathVariable UUID project_id){
        authorizationService.authorize("READ_ROLE");
        List<RoleInfoDto> body = rolesService.getAllRoles(project_id);
        return ResponseEntity.ok(body);
    }
    @PostMapping("/{project_id}")
    public ResponseEntity<RoleInfoDto> createRole(@RequestBody CreateRoleDto role, @PathVariable UUID project_id){
        authorizationService.authorize("CREATE_ROLE");
        RoleInfoDto body = rolesService.createRole(role, project_id);
        return ResponseEntity.ok(body);
    }
    @PatchMapping("/edit/{roleId}/{projectId}")
    public ResponseEntity<RoleInfoDto> updateRole(@PathVariable UUID roleId, @PathVariable UUID projectId, @RequestBody RoleInfoDto request) {
        authorizationService.authorize("UPDATE_ROLE");
        RoleInfoDto body = rolesService.updateRole(roleId, projectId, request);
        return ResponseEntity.ok(body);
    }
    @DeleteMapping("/{project_id}/{role_id}")
    public ResponseEntity<?> deleteRole(@PathVariable UUID role_id, @PathVariable UUID project_id){
        authorizationService.authorize("DELETE_ROLE");
        rolesService.deleteRole(role_id, project_id);
        return ResponseEntity.noContent().build();
    }
}
