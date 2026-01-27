package net.ravik_cms.ravik_backend.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
public class RolesController {
    private final RolesService rolesService;
    @PostMapping("/assign-permission/{roleId}/{permId}")
    public void assignRole(@PathVariable Long roleId, @PathVariable Long permId){
        rolesService.setRole(roleId, permId);
    }
    @GetMapping("/{id}")
    public RoleInfoDto getRole(@PathVariable Long id){
        return rolesService.getRole(id);
    }
    @GetMapping
    public List<RoleInfoDto> getAllRoles(){
        return rolesService.getAllRoles();
    }
    @GetMapping("/name/{name}")
    public RoleInfoDto getRoleByName(@PathVariable String name){
        return rolesService.getRoleByName(name);
    }
    @PostMapping
    public RoleInfoDto createRole(@RequestBody CreateRoleDto role){
        return rolesService.createRole(role);
    }
}
