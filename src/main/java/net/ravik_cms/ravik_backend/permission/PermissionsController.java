package net.ravik_cms.ravik_backend.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final PermissionService permissionService;

    @RequirePermission("READ_ROLE")
    @GetMapping
    public ResponseEntity<List<PermissionInfoDto>> getAllPermissions() {
        List<PermissionInfoDto> body = permissionService.getAllPermissions();
        return ResponseEntity.ok(body);
    }
}
