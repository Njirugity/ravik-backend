package net.ravik_cms.ravik_backend.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/register")
    public SupervisorDto addClient(@Valid @RequestBody CreateClientDto client){
        return userService.addClient(client);
    }

    @PostMapping("/add/supervisor/{id}")
    public SupervisorDto addSupervisor(@Valid @RequestBody CreateSupervisorsDto supervisor, @PathVariable UUID id){
        return userService.addSupervisor(supervisor, id);
    }
    @PostMapping("/add/labourer/{id}")
    public LabourerDto addLabourer(@Valid @RequestBody CreateLabourerDto labourer, @PathVariable UUID id){
        return userService.addLabourer(labourer, id);
    }
    @GetMapping("/clients")
    public List<SupervisorDto> getAllClients(){
        return userService.getAllClients();
    }

    @GetMapping("/supervisor/{project_id}/{user_id}")
    public SupervisorDto getSupervisor(@PathVariable UUID project_id, @PathVariable UUID user_id){
        return userService.getSupervisor(project_id, user_id);
    }

    @GetMapping("/labourer/{project_id}/{user_id}")
    public LabourerDto getLabourer(@PathVariable UUID project_id, @PathVariable UUID user_id){
        return userService.getLabourer(project_id, user_id);
    }

    @GetMapping("/staff/{id}")
    public List<StaffDto> getStaff(@PathVariable UUID id, @RequestParam(required = false) String role){
        if(role != null && !role.isEmpty()){
            return userService.getAllStaffByProjectAndRoles(id, role);
        }else{
            return userService.getAllStaffByProject(id);
        }
    }

    @PatchMapping("edit/{project_id}/{user_id}")
    public StaffDto editStaff(@PathVariable UUID project_id, @PathVariable UUID user_id,
                              @RequestBody StaffDto request){
        return userService.updateUser(project_id, user_id, request);
    }

    @DeleteMapping("delete/{project_id}/{user_id}")
    public void deleteUser(@PathVariable UUID project_id, @PathVariable UUID user_id){
        userService.deleteUser(project_id, user_id);
    }

}
