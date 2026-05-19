package net.ravik_cms.ravik_backend.authorization;


import jakarta.inject.Provider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.security.UserPrincipal;
import net.ravik_cms.ravik_backend.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserProjectFilter extends OncePerRequestFilter {
    private final Provider<UserProjectContext> userProjectContextProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // Skip the filter for login and any other public endpoints
        return path.equals("/auth/login") || path.equals("auth/register");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        UserProjectContext userProjectContext = userProjectContextProvider.get();
        //Extract projectId from Header
        String headerId = request.getHeader("X-Project-ID");
        if (headerId != null){
            userProjectContext.setProjectId(UUID.fromString(headerId));
        }
        //Extract userId from Spring security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal){
            userProjectContext.setUserId(principal.getId());
        }

        filterChain.doFilter(request, response);
    }
}
