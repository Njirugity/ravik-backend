package net.ravik_cms.ravik_backend.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/staff")
public class UserController {
    private final UserService userService;

    @PostMapping("/supervisors/{id}")
    public ResponseEntity<SupervisorDto> addSupervisor(@Valid @RequestBody CreateSupervisorsDto supervisor, @PathVariable UUID id){
        SupervisorDto body = userService.addSupervisor(supervisor, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    @PostMapping("/field_crews/{id}")
    public ResponseEntity<LabourerDto> addLabourer(@Valid @RequestBody CreateLabourerDto labourer, @PathVariable UUID id){
        LabourerDto body = userService.addLabourer(labourer, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
    @GetMapping("/client/{project_id}/{user_id}")
    public ResponseEntity<SupervisorDto> getAllClients(@PathVariable UUID project_id, @PathVariable UUID user_id){
        SupervisorDto body = userService.getClients(project_id, user_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/supervisor/{project_id}/{user_id}")
    public ResponseEntity<SupervisorDto> getSupervisor(@PathVariable UUID project_id, @PathVariable UUID user_id){
        SupervisorDto body = userService.getSupervisor(project_id, user_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/field_crew/{project_id}/{user_id}")
    public ResponseEntity<LabourerDto> getLabourer(@PathVariable UUID project_id, @PathVariable UUID user_id){
        LabourerDto body = userService.getLabourer(project_id, user_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/clients/{project_id}")
    public ResponseEntity<List<SupervisorWithRoleDto>> getClients(@PathVariable UUID project_id){
        List<SupervisorWithRoleDto> body = userService.getAllManagement(project_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/supervisors/{project_id}")
    public ResponseEntity<List<SupervisorWithRoleDto>> getAllSupervisors(@PathVariable UUID project_id){
        List<SupervisorWithRoleDto> body = userService.getAllSupervisors(project_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/field_crews/{project_id}")
    public ResponseEntity<List<LabourerWithRoleDto>> getAllFieldCrews(@PathVariable UUID project_id){
        List<LabourerWithRoleDto> body = userService.getAllFieldCrew(project_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<List<StaffDto>> getStaff(@PathVariable UUID projectId, @RequestParam(required = false) String role){
        if(role != null && !role.isEmpty()){
            List<StaffDto> body = userService.getAllStaffByProjectAndRoles(projectId, role);
            return ResponseEntity.ok(body);
        }else{
            List<StaffDto> body = userService.getAllStaffByProject(projectId);
            return ResponseEntity.ok(body);
        }
    }

    @PatchMapping("edit/{project_id}/{user_id}")
    public ResponseEntity<StaffDto> editStaff(@PathVariable UUID project_id, @PathVariable UUID user_id,
                              @RequestBody StaffDto request){
        StaffDto body = userService.updateUser(project_id, user_id, request);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("delete/{project_id}/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID project_id, @PathVariable UUID user_id){
        userService.deleteUser(project_id, user_id);
        return ResponseEntity.noContent().build();
    }

}
