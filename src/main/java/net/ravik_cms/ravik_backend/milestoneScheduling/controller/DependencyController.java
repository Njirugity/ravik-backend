package net.ravik_cms.ravik_backend.milestoneScheduling.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.DependencyService;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.DependenciesDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.DependencyDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.PredecessorRequest;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.ProjectDependenciesDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dependencies")
@RequiredArgsConstructor
public class DependencyController {
    private final DependencyService dependencyService;

    @RequirePermission("CREATE_DEPENDENCIES")
    @PostMapping("/{milestoneId}/predecessors")
    public ResponseEntity<?> addPredecessor(@PathVariable UUID milestoneId, @RequestBody PredecessorRequest request){
        dependencyService.addPredecessor(milestoneId, request.getPredecessorId());
        return ResponseEntity.ok().build();
    }
    @RequirePermission("DELETE_DEPENDENCIES")
    @DeleteMapping("/{milestoneId}/predecessors/{predecessorId}")
    public ResponseEntity<?> removePredecessor(@PathVariable UUID milestoneId, @PathVariable UUID predecessorId){
        dependencyService.removePredecessor(milestoneId, predecessorId);
        return ResponseEntity.ok().build();
    }
    @RequirePermission("READ_DEPENDENCIES")
    @GetMapping("/{milestoneId}/predecessors")
    public ResponseEntity<List<DependencyDto>> getPredecessor(@PathVariable UUID milestoneId){
        List<DependencyDto> body = dependencyService.getPredecessors(milestoneId);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("READ_DEPENDENCIES")
    @GetMapping("/{milestoneId}/successors")
    public ResponseEntity<List<DependencyDto>> getSuccessors(@PathVariable UUID milestoneId){
        List<DependencyDto> body = dependencyService.getSuccessors(milestoneId);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("READ_DEPENDENCIES")
    @GetMapping("/{projectId}/milestones")
    public ResponseEntity<List<DependenciesDto>> getMilestonesDependents(@PathVariable UUID projectId){
        List<DependenciesDto> body =  dependencyService.getMilestoneDependents(projectId);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("READ_DEPENDENCIES")
    @GetMapping("{projectId}/project")
    public ResponseEntity<ProjectDependenciesDto> getProjectDependencies(@PathVariable UUID projectId){
        ProjectDependenciesDto body = dependencyService.getProjectDependencies(projectId);
        return ResponseEntity.ok(body);
    }
    @RequirePermission("UPDATE_DEPENDENCIES")
    @PutMapping("/{milestoneId}/predecessors")
    public ResponseEntity<?> updateAllPredecessors(@PathVariable UUID milestoneId,
                                                   @RequestBody List<UUID> predecessorId){
        dependencyService.updateAllPredecessors(milestoneId, predecessorId);
        return ResponseEntity.ok().build();
    }

}
