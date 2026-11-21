package net.ravik_cms.ravik_backend.account.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.exception.AccessDeniedException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Accounts belong to a client and are shared by all of that client's projects. A permission
 * check only proves the caller's role in the current project, so every account access must also
 * confirm the account (or client id in the URL) belongs to that project's client.
 */
@Component
@RequiredArgsConstructor
public class AccountAccessGuard {
    private final UserProjectContext context;
    private final ProjectsRepository projectsRepository;
    private final AccountRepository accountRepository;

    /** The client that owns the project in the {@code X-Project-ID} header. */
    public UUID currentClientId() {
        return clientIdOf(context.getProjectId());
    }

    /**
     * @throws AccessDeniedException if {@code clientId} isn't the current project's client
     */
    public void requireCurrentClient(UUID clientId) {
        if (clientId == null || !clientId.equals(currentClientId())) {
            throw new AccessDeniedException("Access Denied, client does not own this project");
        }
    }

    /**
     * @throws AccessDeniedException if {@code projectId} isn't the project in the request header
     */
    public void requireCurrentProject(UUID projectId) {
        if (projectId == null || !projectId.equals(context.getProjectId())) {
            throw new AccessDeniedException("Access Denied, project does not match the current project");
        }
    }

    /**
     * Loads an account, but only if it belongs to the client of {@code projectId}. Another
     * client's account is reported as not found rather than forbidden, so ids can't be probed.
     *
     * @throws ResourceNotFoundException if the account doesn't exist or belongs to another client
     */
    public Accounts requireAccount(UUID accountId, UUID projectId) {
        UUID clientId = clientIdOf(projectId);
        return accountRepository.findById(accountId)
                .filter(account -> account.getClient() != null && account.getClient().getId().equals(clientId))
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    /** True when {@code userId} is the client of the current project. */
    public boolean isCurrentClient(UUID userId) {
        return userId != null && userId.equals(currentClientId());
    }

    private UUID clientIdOf(UUID projectId) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (project.getClient() == null) {
            throw new ResourceNotFoundException("Project has no client");
        }
        return project.getClient().getId();
    }
}
