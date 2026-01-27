package net.ravik_cms.ravik_backend.roles;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolesService {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;
    private final RolesMapper rolesMapper;

    @Transactional
    public void setRole(Long roleId, Long permId){
        Roles role = rolesRepository.findById(roleId).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        Permissions permissions = permissionsRepository.findById(permId).
                orElseThrow(()-> new ResourceNotFoundException("Permission not found"));
        role.getPermissions().add(permissions);
        rolesRepository.save(role);
    }

    public RoleInfoDto getRole(Long id){
        Roles role = rolesRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        return rolesMapper.toRoleDto(role);
    }
    public RoleInfoDto getRoleByName(String name){
        Roles role = rolesRepository.findByName(name).
                orElseThrow(()-> new ResourceNotFoundException("Role not Found"));
        return rolesMapper.toRoleDto(role);
    }
    public List<RoleInfoDto> getAllRoles(){
        List<Roles> allRoles = rolesRepository.findAll();
        return rolesMapper.toRoleDtoList(allRoles);
    }
    public RoleInfoDto createRole(CreateRoleDto role){
        Roles newRole = rolesMapper.fromCreateRole(role);
        return rolesMapper.toRoleDto(rolesRepository.save(newRole));
    }
}
