package net.ravik_cms.ravik_backend.projects;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasAuthority('CREATE_PROJECT')")
    @PostMapping
    public ProjectInfoDto createProject(@Valid @RequestBody ProjectDto project, @AuthenticationPrincipal UserDetails currentUser){
        return projectService.createProject(project, currentUser.getUsername());
    }

    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping("/{id}")
    public ResponseEntity<ProjectInfoDto> getProject(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser){
        ProjectInfoDto project = projectService.getProject(id, currentUser.getUsername());
        return ResponseEntity.ok(project);
    }
    @PreAuthorize("hasAuthority('READ_PROJECT')")
    @GetMapping
    public ResponseEntity<List<ProjectInfoDto>> getAllProjects(@AuthenticationPrincipal UserDetails currentUser){
        List<ProjectInfoDto> projects = projectService.getAllProjects(currentUser.getUsername());
        return ResponseEntity.ok(projects);
    }
    @PreAuthorize("hasAuthority('UPDATE_PROJECT')")
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectPatchDto> patchProject(@PathVariable UUID id, @RequestBody ProjectPatchDto request,
                                                        @AuthenticationPrincipal UserDetails currentUser){
        ProjectPatchDto patchedProject = projectService.patchProject(id, currentUser.getUsername(),request);
        return ResponseEntity.ok(patchedProject);
    }
    @PreAuthorize("hasAuthority('DELETE_PROJECT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser){
        projectService.deleteProject(id, currentUser.getUsername());
        return ResponseEntity.ok("Project successfully deleted");
    }
}
