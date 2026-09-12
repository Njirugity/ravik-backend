package net.ravik_cms.ravik_backend.authentication;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.jwt.JwtUtils;
import net.ravik_cms.ravik_backend.common.security.UserPrincipal;
import net.ravik_cms.ravik_backend.users.ClientDto;
import net.ravik_cms.ravik_backend.users.CreateClientDto;
import net.ravik_cms.ravik_backend.users.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Login, registration, and the current-user bootstrap endpoint")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final AuthService authService;
    private final UserProjectContext userProjectContext;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtils.generateToken(principal.getId(), authentication.getName());
        System.out.println("found login");
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<ClientDto> registerClient (@Valid @RequestBody CreateClientDto client){
        ClientDto body = userService.addClient(client);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user", description = "Identity, role, and permission set for the authenticated user within the current project (resolved from the X-Project-ID header). No permission check — this is the call the frontend uses to discover what permissions it has.")
    public ResponseEntity<CurrentUserDto> getCurrentUser(){
        CurrentUserDto body = authService.getCurrentUser(userProjectContext.getUserId(), userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }
}
