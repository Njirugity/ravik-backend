package net.ravik_cms.ravik_backend.projects.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;
import net.ravik_cms.ravik_backend.projects.dtos.CreateProjectAndBudgetDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectPatchDto;
import net.ravik_cms.ravik_backend.projects.service.ProjectOrchestrationService;
import net.ravik_cms.ravik_backend.projects.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectOrchestrationService orchestrationService;
    private final AuthorizationService authorizationService;

    @PostMapping
    public ResponseEntity <ProjectInfoDto> createProject(@Valid @RequestBody CreateProjectAndBudgetDto request,
                                                         @AuthenticationPrincipal UserDetails currentUser){
        ProjectDto project = request.projectDto();
        List<CreateBudgetDto> budget = request.budgetDtoList();
        ProjectInfoDto body = orchestrationService.createProject(project, currentUser.getUsername(), budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectInfoDto> getProject(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser){
        authorizationService.authorize("READ_PROJECT");
        ProjectInfoDto project = projectService.getProject(id, currentUser.getUsername());
        return ResponseEntity.ok(project);
    }
    @GetMapping
    public ResponseEntity<List<ProjectInfoDto>> getAllProjects(@AuthenticationPrincipal UserDetails currentUser){
        List<ProjectInfoDto> projects = projectService.getAllProjects(currentUser.getUsername());
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectPatchDto> patchProject(@PathVariable UUID id, @RequestBody ProjectPatchDto request,
                                                        @AuthenticationPrincipal UserDetails currentUser){
        authorizationService.authorize("UPDATE_PROJECT");
        ProjectPatchDto patchedProject = projectService.patchProject(id, currentUser.getUsername(),request);
        return ResponseEntity.ok(patchedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable UUID id, @AuthenticationPrincipal UserDetails currentUser){
        authorizationService.authorize("DELETE_PROJECT");
        projectService.deleteProject(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }
}
