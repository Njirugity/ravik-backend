package net.ravik_cms.ravik_backend.authentication;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.roles.Roles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link AuthService} implementation.
 * <p>
 * Backs {@code GET /api/v1/auth/me} — the "who am I, what can I do" bootstrap call the frontend
 * makes after login, resolving identity, role, and permission set for the current project.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final ProjectMembershipRepository membershipRepository;

    /**
     * Resolves the authenticated user's identity, role, and permission set within a project.
     *
     * @throws ResourceNotFoundException if the user has no membership on that project
     */
    @Override
    public CurrentUserDto getCurrentUser(UUID userId, UUID projectId) {
        ProjectMembership membership = membershipRepository.findByUserIdAndProjectId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project Membership not found"));

        Roles role = membership.getRole();
        List<String> permissions = role.getPermissions().stream()
                .map(Permissions::getName)
                .collect(Collectors.toList());

        return CurrentUserDto.builder()
                .userId(userId)
                .userName(membership.getUser().getUserName())
                .email(membership.getUser().getEmail())
                .projectId(projectId)
                .roleId(role.getId())
                .roleName(role.getName())
                .permissions(permissions)
                .build();
    }
}
