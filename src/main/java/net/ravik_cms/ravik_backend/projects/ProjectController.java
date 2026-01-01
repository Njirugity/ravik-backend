package net.ravik_cms.ravik_backend.projects;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ProjectInfoDto> createProject(@Valid @RequestBody ProjectDto projectDto){
        ProjectInfoDto newProject = projectService.createProject(projectDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newProject.getId())
                .toUri();
        return ResponseEntity.created(location).body(newProject);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectInfoDto> getProject(@PathVariable UUID id){
        ProjectInfoDto project = projectService.getProject(id);
        return ResponseEntity.ok(project);
    }
    @GetMapping
    public ResponseEntity<List<ProjectInfoDto>> getAllProjects(){
        List<ProjectInfoDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProjectPatchDto> patchProject(@PathVariable UUID id, @RequestBody ProjectPatchDto request){
        ProjectPatchDto patchedProject = projectService.patchProject(id, request);
        return ResponseEntity.ok(patchedProject);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id){
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project successfully deleted");
    }

}
