package net.ravik_cms.ravik_backend.authorization;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.AccessDeniedException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final ProjectMembershipRepository membershipRepository;
    private final UserProjectContext context;

    public void authorize(String permission){
        UUID projectId = context.getProjectId();
        UUID userId = context.getUserId();
        ProjectMembership membership = membershipRepository.findByUserIdAndProjectId(projectId, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Membership not found"));
        boolean hasPermission = membership.getRole()
                .getPermissions()
                .stream()
                .anyMatch(p->p.getName().equals(permission));

        if (!hasPermission){
            throw new AccessDeniedException("FORBIDDEN!!. Permission Required");
        }
    }

    public void authorizeByRole(String roleName){
        UUID userId = context.getUserId();
        ProjectMembership membership = membershipRepository.findByUserIdAndRoleName(roleName, userId)
                .orElseThrow(()-> new ResourceNotFoundException("Membership not found"));

        if(!membership.getRole().getName().equals(roleName)){
            throw new AccessDeniedException("FORBIDDEN!!. Unauthorized Role");
        }
    }
}
