package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.RoleType;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.ravik_cms.ravik_backend.common.dataInitializer.DefaultPermissions.crud;

@Service
@RequiredArgsConstructor
public class RolesSeeder {
    private final RolesRepository rolesRepository;
    private final PermissionsRepository permissionsRepository;

    private static final List<String> WAGES = List.of("CREATE_WAGE", "READ_WAGE", "DELETE_WAGE");

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
        createRole("OWNER", projects, RoleType.SYSTEM, permissions(DefaultPermissions.ALL));
        createRole("DIRECTOR", projects, RoleType.SYSTEM, permissions(
                DefaultPermissions.ALL_READ,
                crud("PROJECT"), crud("USER"), WAGES
        ));
        createRole("ACCOUNTANT", projects, RoleType.SYSTEM, permissions(
                List.of("READ_PROJECT", "READ_PHASE", "READ_MILESTONE", "READ_SCHEDULE", "READ_MATERIALS_REPORT"),
                crud("USER"), WAGES,
                crud("BUDGET"), crud("PAYMENT"), crud("PAYOUT"), crud("INCOME"), crud("EXPENSE"), crud("ACCOUNT")
        ));
        createRole("ADMIN", projects, RoleType.SYSTEM, permissions(
                List.of("READ_PROJECT", "READ_PHASE", "READ_MILESTONE", "UPLOAD_FILE", "READ_FILE"),
                crud("USER"), WAGES,
                crud("ATTENDANCE"), crud("LABOUR")
        ));
    }

    @SafeVarargs
    private static Set<String> permissions(Collection<String>... groups){
        return Stream.of(groups).flatMap(Collection::stream).collect(Collectors.toSet());
    }
}
