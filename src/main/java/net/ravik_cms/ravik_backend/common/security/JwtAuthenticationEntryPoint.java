package net.ravik_cms.ravik_backend.common.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Replaces Spring Security's default {@code Http403ForbiddenEntryPoint} (empty 403 body) with a
 * JSON 401 for any request that reaches a protected endpoint without a valid access token —
 * missing, malformed, expired, or bad-signature tokens all land here, since
 * {@link net.ravik_cms.ravik_backend.common.jwt.JwtAuthenticationFilter} treats them identically
 * (it just leaves the request unauthenticated rather than rejecting it itself).
 * <p>
 * Writes the body by hand rather than via Jackson/{@code ApiErrors} — this is the security
 * filter chain, outside Spring MVC's message converters, and the message/shape here is fixed and
 * known to be safe to inline.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String body = """
                {"message":"Authentication required","status":401,"timestamp":"%s"}""".formatted(LocalDate.now());
        response.getWriter().write(body);
    }
}
