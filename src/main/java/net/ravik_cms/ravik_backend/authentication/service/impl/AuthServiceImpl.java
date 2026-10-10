package net.ravik_cms.ravik_backend.authentication.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authentication.dto.CurrentUserDto;
import net.ravik_cms.ravik_backend.authentication.dto.LoginRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginResponse;
import net.ravik_cms.ravik_backend.authentication.dto.RefreshTokenResult;
import net.ravik_cms.ravik_backend.authentication.service.AuthService;
import net.ravik_cms.ravik_backend.authentication.service.RefreshTokenService;
import net.ravik_cms.ravik_backend.common.exception.InvalidCredentialsException;
import net.ravik_cms.ravik_backend.common.exception.InvalidTokenException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.jwt.JwtUtils;
import net.ravik_cms.ravik_backend.common.security.UserPrincipal;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link AuthService} implementation.
 * <p>
 * Backs {@code GET /api/v1/auth/me} — the "who am I, what can I do" bootstrap call the frontend
 * makes after login, resolving identity, role, and permission set for the current project — plus
 * the login/refresh/logout flow that issues and rotates access and refresh tokens.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final ProjectMembershipRepository membershipRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

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

    /**
     * Authenticates the given credentials and issues a fresh access/refresh token pair.
     *
     * @throws InvalidCredentialsException if the username/password combination is wrong
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userName(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        assert principal != null;
        String accessToken = jwtUtils.generateToken(principal.getId(), authentication.getName());
        String refreshToken = refreshTokenService.issue(principal.getId());
        return buildLoginResponse(accessToken, refreshToken);
    }

    /**
     * Rotates a refresh token and issues a new access/refresh token pair for its owning user.
     *
     * @throws InvalidTokenException if the refresh token is unknown, already used, or expired
     * @throws ResourceNotFoundException if the owning user no longer exists
     */
    @Override
    public LoginResponse refresh(String refreshToken) {
        RefreshTokenResult rotation = refreshTokenService.rotate(refreshToken);
        Users user = userRepository.findById(rotation.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String accessToken = jwtUtils.generateToken(user.getId(), user.getUserName());
        return buildLoginResponse(accessToken, rotation.rawToken());
    }

    /**
     * Revokes a refresh token, ending the session it belongs to.
     */
    @Override
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    private LoginResponse buildLoginResponse(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getExpirationMs() / 1000)
                .build();
    }
}
