package net.ravik_cms.ravik_backend.common.dataInitializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class PermissionsSeeder implements CommandLineRunner {
    private final PermissionsRepository permissionsRepository;
    private final RolesRepository rolesRepository;

    @Override
    @Transactional
    public void run(String... args){
        Set<Permissions> created = new HashSet<>();
        for(String pName: DefaultPermissions.ALL){
            if(!permissionsRepository.existsByName(pName)){
                Permissions p = new Permissions();
                p.setName(pName);
                created.add(permissionsRepository.save(p));
            }
        }
        grantToExistingOwners(created);
        log.info("Permissions seeded successfully");
    }

    /**
     * Default roles are only seeded when a project is created, so OWNER roles from before a
     * permission existed never receive it. Only permissions created in this run are added, so a
     * permission a client later removes from their OWNER role isn't put back on every restart.
     */
    private void grantToExistingOwners(Set<Permissions> created){
        if (created.isEmpty()) {
            return;
        }
        for (Roles owner : rolesRepository.findAllByName("OWNER")) {
            owner.getPermissions().addAll(created);
            rolesRepository.save(owner);
        }
    }
}
