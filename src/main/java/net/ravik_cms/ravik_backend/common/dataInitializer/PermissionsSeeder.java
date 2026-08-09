package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionsSeeder implements CommandLineRunner {
    private final PermissionsRepository permissionsRepository;

    @Override
    @Order(2)
    public void run(String... args){
        List<String> permissions = List.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER"
        );
        for(String pName: permissions){
            if(!permissionsRepository.existsByName(pName)){
                Permissions p = new Permissions();
                p.setName(pName);
                permissionsRepository.save(p);
            }
        }
        System.out.println("Permissions seeded successfully");
    }

}
