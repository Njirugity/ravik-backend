package net.ravik_cms.ravik_backend.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/staff")
public class UserController {
    private final UserService userService;

    @PostMapping("/add/supervisor/{id}")
    public SupervisorDto addSupervisor(@Valid @RequestBody CreateSupervisorsDto supervisor, @PathVariable UUID id){
        return userService.addSupervisor(supervisor, id);
    }
    @PostMapping("/add/labourer/{id}")
    public LabourerDto addLabourer(@Valid @RequestBody CreateLabourerDto labourer, @PathVariable UUID id){
        return userService.addLabourer(labourer, id);
    }
    @GetMapping("/client/{project_id}/{user_id}")
    public SupervisorDto getAllClients(@PathVariable UUID project_id, @PathVariable UUID user_id){
        return userService.getClients(project_id, user_id);
    }

    @GetMapping("/supervisor/{project_id}/{user_id}")
    public SupervisorDto getSupervisor(@PathVariable UUID project_id, @PathVariable UUID user_id){
        return userService.getSupervisor(project_id, user_id);
    }

    @GetMapping("/labourer/{project_id}/{user_id}")
    public LabourerDto getLabourer(@PathVariable UUID project_id, @PathVariable UUID user_id){
        return userService.getLabourer(project_id, user_id);
    }
    @GetMapping("/clients/{project_id}")
    public List<SupervisorWithRoleDto> getClients(@PathVariable UUID project_id){
        return userService.getAllManagement(project_id);
    }
    @GetMapping("/supervisors/{project_id}")
    public List<SupervisorWithRoleDto> getAllSupervisors(@PathVariable UUID project_id){
        return userService.getAllSupervisors(project_id);
    }
    @GetMapping("/field_crews/{project_id}")
    public List<LabourerWithRoleDto> getAllFieldCrews(@PathVariable UUID project_id){
        return userService.getAllFieldCrew(project_id);
    }
    @GetMapping("/{projectId}")
    public List<StaffDto> getStaff(@PathVariable UUID projectId, @RequestParam(required = false) String role){
        if(role != null && !role.isEmpty()){
            return userService.getAllStaffByProjectAndRoles(projectId, role);
        }else{
            return userService.getAllStaffByProject(projectId);
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
