package net.ravik_cms.ravik_backend.roles;

import net.ravik_cms.ravik_backend.account.service.AccountAccessGuard;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.dataInitializer.DefaultPermissions;
import net.ravik_cms.ravik_backend.common.exception.AccessDeniedException;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RolesServicePermissionGrantTest {

    private static final String SUMMARY = DefaultPermissions.VIEW_CLIENT_ACCOUNT_SUMMARY;

    private AccountAccessGuard guard;
    private RolesRepository rolesRepository;
    private RolesService rolesService;
    private final Roles role = new Roles();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        rolesRepository = mock(RolesRepository.class);
        PermissionsRepository permissionsRepository = mock(PermissionsRepository.class);
        ProjectsRepository projectsRepository = mock(ProjectsRepository.class);
        guard = mock(AccountAccessGuard.class);

        Projects project = new Projects();
        project.setId(UUID.randomUUID());
        role.setId(UUID.randomUUID());
        role.setPermissions(new HashSet<>());
        when(projectsRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(rolesRepository.findByIdAndProject(role.getId(), project)).thenReturn(Optional.of(role));
        when(permissionsRepository.findByNameIn(any())).thenReturn(Set.of());

        rolesService = new RolesService(rolesRepository, permissionsRepository, mock(RolesMapper.class),
                projectsRepository, new UserProjectContext(userId, project.getId()), guard);
    }

    private void roleHas(String name) {
        Permissions p = new Permissions();
        p.setName(name);
        role.getPermissions().add(p);
    }

    @Test
    void nonClientCannotGrantTheSummaryPermission() {
        when(guard.isCurrentClient(userId)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> rolesService.setPermissions(role.getId(), Set.of(SUMMARY)));
        verify(rolesRepository, never()).save(any());
    }

    @Test
    void nonClientCannotRevokeTheSummaryPermission() {
        roleHas(SUMMARY);
        when(guard.isCurrentClient(userId)).thenReturn(false);
        assertThrows(AccessDeniedException.class, () -> rolesService.setPermissions(role.getId(), Set.of("READ_ACCOUNT")));
    }

    @Test
    void nonClientCanChangeOtherPermissionsWhileLeavingTheSummaryAlone() {
        roleHas(SUMMARY);
        when(guard.isCurrentClient(userId)).thenReturn(false);
        assertDoesNotThrow(() -> rolesService.setPermissions(role.getId(), Set.of(SUMMARY, "READ_ACCOUNT")));
    }

    @Test
    void clientCanGrantTheSummaryPermission() {
        when(guard.isCurrentClient(userId)).thenReturn(true);
        assertDoesNotThrow(() -> rolesService.setPermissions(role.getId(), Set.of(SUMMARY)));
        verify(rolesRepository).save(role);
    }
}
