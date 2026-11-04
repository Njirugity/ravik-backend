package net.ravik_cms.ravik_backend.phase.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.phase.dtos.*;
import net.ravik_cms.ravik_backend.phase.service.PhasesService;
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
@RequestMapping("api/v1/phases")
public class PhasesController {
    private final PhasesService phasesService;
    private final AuthorizationService authorizationService;

    @PostMapping("/{project_id}")
    public ResponseEntity<PhasesInfoDto> addPhase(@PathVariable UUID project_id, @RequestBody CreatePhaseDto request){
        authorizationService.authorize("CREATE_PHASE");
        PhasesInfoDto body = phasesService.createPhase(project_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{phase_id}")
    public ResponseEntity<PhasesInfoDto> getPhase( @PathVariable UUID phase_id){
        authorizationService.authorize("READ_PHASE");
        PhasesInfoDto body = phasesService.getPhase( phase_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<PhasesInfoDto>> getPhases(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable){
        authorizationService.authorize("READ_PHASE");
        Page<PhasesInfoDto> body= phasesService.getPhases(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{phase_id}")
    public ResponseEntity<PhasesInfoDto> updatePhase(@PathVariable UUID phase_id,
                                     @RequestBody UpdatePhaseDto request){
        authorizationService.authorize("UPDATE_PHASE");
        PhasesInfoDto body = phasesService.updatePhase(phase_id, request);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{phase_id}")
    public ResponseEntity<?> deletePhase( @PathVariable UUID phase_id) {
        authorizationService.authorize("DELETE_PHASE");
        phasesService.deletePhase( phase_id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{phase_id}/milestone/assign")
    public ResponseEntity<?> assignMilestone(@PathVariable UUID phase_id,
                                             @RequestBody List<MilestoneAssignmentDto> request){
        authorizationService.authorize("UPDATE_PHASE");
        phasesService.assignMilestones(phase_id, request);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/milestone/unassign")
    public ResponseEntity<?> unassignMilestone(@RequestBody List<MilestoneAssignmentDto> request){
        authorizationService.authorize("UPDATE_PHASE");
        phasesService.unassignMilestones(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/{project_id}")
    public ResponseEntity<PhasesInfoDto> activePhase(@PathVariable UUID project_id){
        authorizationService.authorize("READ_PHASE");
        PhasesInfoDto body = phasesService.getActivePhase(project_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("overdue/{phase_id}")
    public ResponseEntity<PhaseOverdueDto> overdue(@PathVariable UUID phase_id){
        authorizationService.authorize("READ_PHASE");
        PhaseOverdueDto body = phasesService.overduePhases(phase_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("infographics/{project_id}")
    public ResponseEntity<PhasesInfographicsDto> infographics(@PathVariable UUID project_id){
        authorizationService.authorize("READ_PHASE");
        PhasesInfographicsDto body = phasesService.getInfographicForPhase(project_id);
        return ResponseEntity.ok(body);
    }
}
