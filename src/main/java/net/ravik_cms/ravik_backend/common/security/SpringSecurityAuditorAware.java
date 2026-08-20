package net.ravik_cms.ravik_backend.common.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Feeds {@code @CreatedBy}/{@code @LastModifiedBy} on {@link net.ravik_cms.ravik_backend.common.baseEntities.BaseEntity}
 * from the authenticated user, mirroring how {@link net.ravik_cms.ravik_backend.authorization.UserProjectContext}
 * resolves the current user id off {@link SecurityContextHolder}.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return Optional.empty();
        }
        return Optional.of(principal.getId());
    }
}
