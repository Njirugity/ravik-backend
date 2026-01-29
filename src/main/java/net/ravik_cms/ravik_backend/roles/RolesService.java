package net.ravik_cms.ravik_backend.roles;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.permission.PermissionInfoDto;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolesService {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;
    private final RolesMapper rolesMapper;
    private final ProjectsRepository projectsRepository;

    @Transactional
    public void setPermissions(UUID roleId, Set<String> permissionsKey) {
        Roles role = rolesRepository.findById(roleId).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        Set<Permissions> permissions = permissionsRepository.findByNameIn(permissionsKey);
        role.setPermissions(permissions);
        rolesRepository.save(role);
    }

    public Roles findRole(UUID roleId, UUID projectId) {
        Projects projects = projectsRepository.findById(projectId).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        return rolesRepository.findByIdAndProject(roleId, projects).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
    }
    public RoleInfoDto getRole(UUID id, UUID projectId){
        Roles role = findRole(id, projectId);
        return rolesMapper.toRoleDto(role);
    }

    public List<RoleInfoDto> getAllRoles(UUID project_id){
        Projects projects = projectsRepository.findById(project_id).orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        List<Roles> allRoles = rolesRepository.findAllByProject(projects);
        return rolesMapper.toRoleDtoList(allRoles);
    }
    public RoleInfoDto createRole(CreateRoleDto role, UUID project_id){
        Projects projects = projectsRepository.findById(project_id).orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Roles newRole = rolesMapper.fromCreateRole(role);
        newRole.setProject(projects);
        return rolesMapper.toRoleDto(rolesRepository.save(newRole));

    }
    public RoleInfoDto updateRole(UUID roleId, UUID projectId, RoleInfoDto request) {
        Roles roles = findRole(roleId, projectId);
        rolesMapper.updateRole(request, roles);
        return rolesMapper.toRoleDto(rolesRepository.save(roles));
    }
    public void deleteRole(UUID roleId, UUID projectId) {
        Roles role = findRole(roleId, projectId);
        rolesRepository.delete(role);
    }
}
