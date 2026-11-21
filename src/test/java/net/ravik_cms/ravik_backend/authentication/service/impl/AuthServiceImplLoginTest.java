package net.ravik_cms.ravik_backend.authentication.service.impl;

import net.ravik_cms.ravik_backend.authentication.dto.LoginRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginResponse;
import net.ravik_cms.ravik_backend.authentication.service.RefreshTokenService;
import net.ravik_cms.ravik_backend.common.exception.InvalidCredentialsException;
import net.ravik_cms.ravik_backend.common.jwt.JwtUtils;
import net.ravik_cms.ravik_backend.common.password.PasswordPolicy;
import net.ravik_cms.ravik_backend.common.security.UserPrincipal;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplLoginTest {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtUtils jwtUtils;
    private AuthServiceImpl authService;
    private final Users user = new Users();

    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        userRepository = mock(UserRepository.class);
        jwtUtils = mock(JwtUtils.class);
        RefreshTokenService refreshTokenService = mock(RefreshTokenService.class);
        authService = new AuthServiceImpl(mock(ProjectMembershipRepository.class), authenticationManager, jwtUtils,
                refreshTokenService, userRepository, mock(PasswordPolicy.class), mock(PasswordEncoder.class));

        user.setId(UUID.randomUUID());
        user.setUserName("admin");
        user.setEmail("admin@ravik.local");

        when(jwtUtils.generateToken(any(), any())).thenReturn("access");
        when(jwtUtils.getExpirationMs()).thenReturn(900000L);
        when(refreshTokenService.issue(any())).thenReturn("refresh");
    }

    private void acceptPassword(String password) {
        when(authenticationManager.authenticate(any())).thenAnswer(inv -> {
            UsernamePasswordAuthenticationToken token = inv.getArgument(0);
            if (!password.equals(token.getCredentials())) {
                throw new BadCredentialsException("bad");
            }
            UserPrincipal principal = new UserPrincipal(user.getId(), (String) token.getPrincipal(), "hash");
            return new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
        });
    }

    @Test
    void logsInByEmailAndAuthenticatesAsUsername() {
        when(userRepository.findByEmailIgnoreCase("admin@ravik.local")).thenReturn(Optional.of(user));
        acceptPassword("Password123!");

        LoginResponse response = authService.login(new LoginRequest("admin@ravik.local", "Password123!"));

        assertEquals("access", response.token());
        verify(authenticationManager).authenticate(argThat(t -> "admin".equals(t.getPrincipal())));
        verify(jwtUtils).generateToken(user.getId(), "admin");
    }

    @Test
    void trimsEmailBeforeLookup() {
        when(userRepository.findByEmailIgnoreCase("Admin@Ravik.local")).thenReturn(Optional.of(user));
        acceptPassword("Password123!");

        authService.login(new LoginRequest("  Admin@Ravik.local ", "Password123!"));

        verify(userRepository).findByEmailIgnoreCase("Admin@Ravik.local");
    }

    @Test
    void unknownEmailIsInvalidCredentials() {
        when(userRepository.findByEmailIgnoreCase(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("nobody@ravik.local", "Password123!")));
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void wrongPasswordIsInvalidCredentials() {
        when(userRepository.findByEmailIgnoreCase("admin@ravik.local")).thenReturn(Optional.of(user));
        acceptPassword("Password123!");

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginRequest("admin@ravik.local", "wrong")));
    }
}
