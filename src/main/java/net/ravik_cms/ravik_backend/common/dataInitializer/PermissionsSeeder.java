package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.permission.Permissions;
import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Order(2)
@Component
@RequiredArgsConstructor
public class PermissionsSeeder implements CommandLineRunner {
    private final PermissionsRepository permissionsRepository;

    @Override
    public void run(String... args){
        List<String> permissions = List.of(
                "CREATE_PROJECT", "READ_PROJECT", "UPDATE_PROJECT", "DELETE_PROJECT",
                "CREATE_USER", "READ_USER", "UPDATE_USER", "DELETE_USER", "CREATE_MILESTONE",
                "READ_MILESTONE", "UPDATE_MILESTONE", "DELETE_MILESTONE", "CREATE_SCHEDULE",
                "READ_SCHEDULE", "UPDATE_SCHEDULE", "DELETE_SCHEDULE","CREATE_DEPENDENCIES",
                "READ_DEPENDENCIES", "UPDATE_DEPENDENCIES", "DELETE_DEPENDENCIES",
                "CREATE_ATTENDANCE", "READ_ATTENDANCE", "UPDATE_ATTENDANCE", "DELETE_ATTENDANCE",
                "CREATE_WAGE", "READ_WAGE", "DELETE_WAGE"
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
