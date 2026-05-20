package net.ravik_cms.ravik_backend.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.ravik_cms.ravik_backend.common.jwt.JwtUtils;
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
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        String token = jwtUtils.generateToken(authentication.getName());
        System.out.println("found login");
        return ResponseEntity.ok(new LoginResponse(token));
    }
    @PostMapping("/register")
    public ResponseEntity<ClientDto> registerClient (@Valid @RequestBody CreateClientDto client){
        ClientDto body = userService.addClient(client);
        return ResponseEntity.ok(body);
    }
}
