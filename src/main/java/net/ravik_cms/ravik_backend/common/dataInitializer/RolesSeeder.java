package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.enums.RoleType;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
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
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"
        ));
        createRole("DIRECTOR", projects, RoleType.SYSTEM,Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"
        ));
        createRole("ACCOUNTANT", projects,RoleType.SYSTEM, Set.of(
                "READ_PROJECT", "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"
        ));
        createRole("ADMIN", projects, RoleType.SYSTEM,Set.of(
                "READ_PROJECT", "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"
        ));

    }
}
