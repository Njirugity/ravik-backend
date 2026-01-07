package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class RolesSeeder implements CommandLineRunner {
    private final RolesRepository rolesRepository;

    @Override
    public void run(String... args){
        List<String> roles = List.of(
                "ADMIN", "CONTRACTOR", "CONSTRUCTION_MANAGER", "MASON", "CARPENTER", "CASUAL"
        );

        for(String rName : roles){
            if(!rolesRepository.existsByName(rName)){
                Roles r = new Roles();
                r.setName(rName);
                rolesRepository.save(r);
            }
        }
        System.out.println("Roles seeded successfully");
    }
}
