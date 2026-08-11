package net.ravik_cms.ravik_backend.users;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addUser(@PathVariable UUID project_id, @RequestBody CreateUserDto request){
        userService.addUser(project_id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<UserDetailsProjection>> getAllUsers(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable){
        Page<UserDetailsProjection> body = userService.getAllUsers(project_id, role, jobTitle, search, pageable);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{project_id}/{user_id}")
    public ResponseEntity<UserDetailDto> getUserDetail(@PathVariable UUID project_id, @PathVariable UUID user_id){
        UserDetailDto body = userService.getUserDetail(project_id, user_id);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("edit/{project_id}/{user_id}")
    public ResponseEntity<UserDetailDto> editStaff(@PathVariable UUID project_id, @PathVariable UUID user_id,
                              @RequestBody UpdateUserDto request){
        UserDetailDto body = userService.updateUser(project_id, user_id, request);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("delete/{project_id}/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID project_id, @PathVariable UUID user_id){
        userService.deleteUser(project_id, user_id);
        return ResponseEntity.noContent().build();
    }

}
