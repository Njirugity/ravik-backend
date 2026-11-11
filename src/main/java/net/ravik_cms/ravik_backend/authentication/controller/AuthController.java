package net.ravik_cms.ravik_backend.authentication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.ravik_cms.ravik_backend.authentication.dto.ChangePasswordRequest;
import net.ravik_cms.ravik_backend.authentication.dto.CurrentUserDto;
import net.ravik_cms.ravik_backend.authentication.dto.ForgotPasswordRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginResponse;
import net.ravik_cms.ravik_backend.authentication.dto.RefreshRequest;
import net.ravik_cms.ravik_backend.authentication.dto.ResetPasswordRequest;
import net.ravik_cms.ravik_backend.authentication.dto.ValidateOtpRequest;
import net.ravik_cms.ravik_backend.authentication.service.AuthService;
import net.ravik_cms.ravik_backend.authentication.service.PasswordResetService;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.users.ClientDto;
import net.ravik_cms.ravik_backend.users.CreateClientDto;
import net.ravik_cms.ravik_backend.users.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Login, registration, token refresh/logout, and the current-user bootstrap endpoint")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final UserProjectContext userProjectContext;

    @PostMapping("/login")
    @Operation(summary = "Log in", description = "Authenticates credentials and returns an access token, a refresh token, and the access token's lifetime in seconds.")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the access token", description = "Exchanges a still-valid, not-yet-used refresh token for a new access token and a rotated refresh token. The presented refresh token is revoked as part of the exchange.")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshRequest request){
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out", description = "Revokes the given refresh token, ending the session it belongs to.")
    public ResponseEntity<ApiMessageResponse> logout(@RequestBody RefreshRequest request){
        authService.logout(request.refreshToken());
        return ResponseEntity.ok(ApiMessageResponse.of("Logged out successfully", HttpStatus.OK.value()));
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

    @PostMapping("/change-password")
    @Operation(summary = "Change password (logged in)", description = "Verifies the current password, checks the new one against the strength policy, and saves it. Optionally revokes every other refresh token the user holds, keeping the session identified by refreshToken (if given) alive.")
    public ResponseEntity<ApiMessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        authService.changePassword(userProjectContext.getUserId(), request.currentPassword(), request.newPassword(),
                request.terminateOtherSessions(), request.refreshToken());
        return ResponseEntity.ok(ApiMessageResponse.of("Password changed successfully", HttpStatus.OK.value()));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset code", description = "Emails a one-time code to the account's registered email. Always returns success, whether or not the username exists, to avoid revealing which usernames are registered.")
    public ResponseEntity<ApiMessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        passwordResetService.forgotPassword(request.userName());
        return ResponseEntity.ok(ApiMessageResponse.of(
                "If that account exists, a reset code has been sent to its registered email", HttpStatus.OK.value()));
    }

    @PostMapping("/validate-otp")
    @Operation(summary = "Validate a password reset code", description = "Checks the code emailed by /forgot-password. Must succeed before /reset-password will accept a new password.")
    public ResponseEntity<ApiMessageResponse> validateOtp(@Valid @RequestBody ValidateOtpRequest request){
        passwordResetService.validateOtp(request.userName(), request.otp());
        return ResponseEntity.ok(ApiMessageResponse.of("Code verified", HttpStatus.OK.value()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password after OTP verification", description = "Sets a new password for a user whose code was already confirmed via /validate-otp. Optionally revokes every refresh token the user holds.")
    public ResponseEntity<ApiMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        passwordResetService.resetPassword(request.userName(), request.newPassword(), request.terminateOtherSessions());
        return ResponseEntity.ok(ApiMessageResponse.of("Password reset successfully", HttpStatus.OK.value()));
    }
}
