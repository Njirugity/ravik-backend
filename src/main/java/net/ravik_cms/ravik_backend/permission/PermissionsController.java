package net.ravik_cms.ravik_backend.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionsController {
    private final PermissionService permissionService;

    @GetMapping
    public List<PermissionInfoDto> getAllPermissions() {
        return permissionService.getAllPermissions();
    }
}
