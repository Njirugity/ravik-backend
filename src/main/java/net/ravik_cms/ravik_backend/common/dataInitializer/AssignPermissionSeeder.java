//package net.ravik_cms.ravik_backend.common.dataInitializer;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
//import net.ravik_cms.ravik_backend.permission.Permissions;
//import net.ravik_cms.ravik_backend.permission.PermissionsRepository;
//import net.ravik_cms.ravik_backend.roles.Roles;
//import net.ravik_cms.ravik_backend.roles.RolesRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//public class AssignPermissionSeeder implements CommandLineRunner {
//    private final RolesRepository rolesRepository;
//    private final PermissionsRepository permissionsRepository;
//
//    public void adminRoles(){
//        Roles admin = rolesRepository.findByName("ADMIN").
//                orElseThrow(()-> new ResourceNotFoundException("ADMIN not found"));
//        List<Permissions> allPermission = permissionsRepository.findAll();
//        admin.getPermissions().addAll(allPermission);
//        rolesRepository.save(admin);
//        System.out.println("Admin roles assigned successfully");
//    }
//    public void contractorRoles(){
//        Roles contractor = rolesRepository.findByName("CONTRACTOR").
//                orElseThrow(()-> new ResourceNotFoundException("CONTRACTOR not found"));
//        Set<Permissions> allowedPermissions = permissionsRepository.findByNameIn(List.of("CREATE_SUPERVISOR",
//                "READ_SUPERVISOR", "UPDATE_SUPERVISORS","DELETE_SUPERVISORS","CREATE_LABOURER",
//                "READ_LABOURER", "UPDATE_LABOURER","DELETE_LABOURER"));
//        contractor.getPermissions().addAll(allowedPermissions);
//        rolesRepository.save(contractor);
//        System.out.println("Admin roles assigned successfully");
//    }
//
//    @Override
//    @Transactional
//    @Order(3)
//    public void run(String... args) throws Exception {
//        adminRoles();
//        contractorRoles();
//    }
//}
