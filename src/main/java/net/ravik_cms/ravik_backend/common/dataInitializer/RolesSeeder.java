package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.RoleType;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RolesSeeder {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;

    private void createRole(String name, Projects projects,RoleType type, Set<String> permissionsKey){
        Set<Permissions> permissions = permissionsRepository.findByNameIn(permissionsKey);
        Roles roles = new Roles();
        roles.setName(name);
        roles.setProject(projects);
        roles.setRoleType(type);
        roles.setPermissions(permissions);
        rolesRepository.save(roles);
    }
    public void seedDefaultRoles(Projects projects){
        createRole("OWNER", projects, RoleType.SYSTEM,Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_MATERIAL_LIST", "READ_MATERIAL_LIST", "UPDATE_MATERIAL_LIST", "DELETE_MATERIAL_LIST",
                "CREATE_MATERIAL_REQUIRED", "READ_MATERIAL_REQUIRED", "UPDATE_MATERIAL_REQUIRED", "DELETE_MATERIAL_REQUIRED",
                "CREATE_MATERIAL_DELIVERED", "READ_MATERIAL_DELIVERED", "UPDATE_MATERIAL_DELIVERED", "DELETE_MATERIAL_DELIVERED",
                "CREATE_MATERIAL_USED", "READ_MATERIAL_USED", "UPDATE_MATERIAL_USED", "DELETE_MATERIAL_USED",
                "READ_MATERIALS_REPORT", "READ_MATERIAL_ALERTS","CREATE_MILESTONE",
                "READ_MILESTONE", "UPDATE_MILESTONE", "DELETE_MILESTONE", "CREATE_SCHEDULE",
                "READ_SCHEDULE", "UPDATE_SCHEDULE", "DELETE_SCHEDULE","CREATE_DEPENDENCIES",
                "READ_DEPENDENCIES", "UPDATE_DEPENDENCIES", "DELETE_DEPENDENCIES",
                "CREATE_ATTENDANCE", "READ_ATTENDANCE", "UPDATE_ATTENDANCE", "DELETE_ATTENDANCE",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE", "UPLOAD_FILE", "READ_FILE", "READ_DASHBOARD"
        ));
        createRole("DIRECTOR", projects, RoleType.SYSTEM,Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE", "READ_DASHBOARD"
        ));
        createRole("ACCOUNTANT", projects,RoleType.SYSTEM, Set.of(
                "READ_PROJECT", "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE", "READ_DASHBOARD"
        ));
        createRole("ADMIN", projects, RoleType.SYSTEM,Set.of(
                "READ_PROJECT", "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE", "READ_DASHBOARD"
        ));

    }
}
