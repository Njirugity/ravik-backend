package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RolesSeeder {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;

    private void createRole(String name, Projects projects, Set<String> permissionsKey){
        Set<Permissions> permissions = permissionsRepository.findByNameIn(permissionsKey);
        Roles roles = new Roles();
        roles.setName(name);
        roles.setProject(projects);
        roles.setPermissions(permissions);
        rolesRepository.save(roles);
    }
    public void seedDefaultRoles(Projects projects){
        createRole("OWNER", projects, Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_SUPERVISOR", "READ_SUPERVISOR", "UPDATE_SUPERVISORS",
                "DELETE_SUPERVISORS","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("DIRECTOR", projects, Set.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_SUPERVISOR", "READ_SUPERVISOR", "UPDATE_SUPERVISORS",
                "DELETE_SUPERVISORS","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("CONTRACTOR", projects, Set.of(
                "READ_PROJECT", "CREATE_SUPERVISOR", "READ_SUPERVISOR", "UPDATE_SUPERVISORS",
                "DELETE_SUPERVISORS","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("CONSTRUCTION_MANAGER", projects, Set.of(
                "READ_PROJECT", "CREATE_SUPERVISOR", "READ_SUPERVISOR", "UPDATE_SUPERVISORS",
                "DELETE_SUPERVISORS","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("SITE_MANAGER", projects, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("CLERK_OF_WORKS", projects, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER", "UPDATE_LABOURER",
                "DELETE_LABOURER"
        ));
        createRole("FOREMAN", projects, Set.of(
                "READ_PROJECT", "READ_SUPERVISOR","CREATE_LABOURER", "READ_LABOURER"
        ));
        createRole("MASON", projects, Set.of());
        createRole("CARPENTER", projects, Set.of());
        createRole("OPERATOR", projects, Set.of());
        createRole("CASUAL", projects, Set.of());
    }
}
