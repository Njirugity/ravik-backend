package net.ravik_cms.ravik_backend.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final PermissionService permissionService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<List<PermissionInfoDto>> getAllPermissions() {
        authorizationService.authorize("READ_ROLE");
        List<PermissionInfoDto> body = permissionService.getAllPermissions();
        return ResponseEntity.ok(body);
    }
}
