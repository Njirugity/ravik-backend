package net.ravik_cms.ravik_backend.permission;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionsRepository permissionsRepository;
    private final PermissionsMapper permissionsMapper;

    public List<PermissionInfoDto> getAllPermissions(){
        List<Permissions> allPermissions = permissionsRepository.findAll();
        return permissionsMapper.toPermissionsListDto(allPermissions);
    }
}
