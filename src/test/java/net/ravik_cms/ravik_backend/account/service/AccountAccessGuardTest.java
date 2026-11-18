package net.ravik_cms.ravik_backend.account.service;

import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.common.exception.AccessDeniedException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountAccessGuardTest {

    private AccountRepository accountRepository;
    private AccountAccessGuard guard;
    private final Client client = client();
    private final Projects project = new Projects();

    @BeforeEach
    void setUp() {
        ProjectsRepository projectsRepository = mock(ProjectsRepository.class);
        accountRepository = mock(AccountRepository.class);
        project.setId(UUID.randomUUID());
        project.setClient(client);
        when(projectsRepository.findById(project.getId())).thenReturn(Optional.of(project));
        UserProjectContext context = new UserProjectContext(UUID.randomUUID(), project.getId());
        guard = new AccountAccessGuard(context, projectsRepository, accountRepository);
    }

    private static Client client() {
        Client c = new Client();
        c.setId(UUID.randomUUID());
        return c;
    }

    private Accounts accountOf(Client owner) {
        Accounts account = new Accounts();
        account.setId(UUID.randomUUID());
        account.setClient(owner);
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        return account;
    }

    @Test
    void returnsAccountOwnedByTheProjectsClient() {
        Accounts account = accountOf(client);
        assertSame(account, guard.requireAccount(account.getId(), project.getId()));
    }

    @Test
    void anotherClientsAccountIsNotFound() {
        Accounts foreign = accountOf(client());
        assertThrows(ResourceNotFoundException.class, () -> guard.requireAccount(foreign.getId(), project.getId()));
    }

    @Test
    void clientIdInUrlMustBeTheProjectsClient() {
        assertDoesNotThrow(() -> guard.requireCurrentClient(client.getId()));
        assertThrows(AccessDeniedException.class, () -> guard.requireCurrentClient(UUID.randomUUID()));
    }

    @Test
    void projectIdInUrlMustBeTheCurrentProject() {
        assertDoesNotThrow(() -> guard.requireCurrentProject(project.getId()));
        assertThrows(AccessDeniedException.class, () -> guard.requireCurrentProject(UUID.randomUUID()));
    }

    @Test
    void recognisesTheClient() {
        assertTrue(guard.isCurrentClient(client.getId()));
        assertFalse(guard.isCurrentClient(UUID.randomUUID()));
    }
}
