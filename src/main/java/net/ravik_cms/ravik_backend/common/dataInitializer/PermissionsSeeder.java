package net.ravik_cms.ravik_backend.common.dataInitializer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
public class PermissionsSeeder implements CommandLineRunner {
    private final PermissionsRepository permissionsRepository;

    @Override
    @Transactional
    public void run(String... args){
        for(String pName: DefaultPermissions.ALL){
            if(!permissionsRepository.existsByName(pName)){
                Permissions p = new Permissions();
                p.setName(pName);
                permissionsRepository.save(p);
            }
        }
        log.info("Permissions seeded successfully");
    }
}
