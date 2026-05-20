package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
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

    private void createRole(String name, Projects projects, RoleCategory category, Set<String> permissionsKey){
        Set<Permissions> permissions = permissionsRepository.findByNameIn(permissionsKey);
        Roles roles = new Roles();
        roles.setName(name);
        roles.setProject(projects);
        roles.setRoleCategory(category);
        roles.setSystemDefined(true);
        roles.setPermissions(permissions);
        rolesRepository.save(roles);
    }
    public void seedDefaultRoles(Projects projects){
        createRole("OWNER", projects, RoleCategory.MANAGEMENT, Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_MANAGEMENT", "READ_MANAGEMENT", "READ_STAFF", "UPDATE_STAFF", "DELETE_STAFF",
                "CREATE_SUPERVISOR", "READ_SUPERVISOR",
                "CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("DIRECTOR", projects, RoleCategory.MANAGEMENT, Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_MANAGEMENT", "READ_MANAGEMENT", "READ_STAFF","UPDATE_STAFF", "DELETE_STAFF",
                "CREATE_SUPERVISOR", "READ_SUPERVISOR",
                "CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("CONTRACTOR", projects, RoleCategory.SUPERVISION, Set.of(
                "READ_PROJECT", "CREATE_SUPERVISOR", "READ_SUPERVISOR", "READ_STAFF", "UPDATE_STAFF",
                "DELETE_STAFF","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("CONSTRUCTION_MANAGER", projects, RoleCategory.SUPERVISION, Set.of(
                "READ_PROJECT", "CREATE_SUPERVISOR", "READ_SUPERVISOR","READ_STAFF", "UPDATE_STAFF",
                "DELETE_STAFF","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("SITE_MANAGER", projects,RoleCategory.SUPERVISION, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("CLERK_OF_WORKS", projects,RoleCategory.SUPERVISION, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("FOREMAN", projects,RoleCategory.SUPERVISION, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("MASON", projects,RoleCategory.FIELD_CREW, Set.of());
        createRole("CARPENTER", projects, RoleCategory.FIELD_CREW, Set.of());
        createRole("OPERATOR", projects, RoleCategory.FIELD_CREW, Set.of());
        createRole("CASUAL", projects, RoleCategory.FIELD_CREW, Set.of());
    }
}
