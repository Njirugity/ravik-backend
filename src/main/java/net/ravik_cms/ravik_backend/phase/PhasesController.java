package net.ravik_cms.ravik_backend.phase;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/phases")
public class PhasesController {
    private final PhasesService  phasesService;

    @PostMapping("/{project_id}")
    public ResponseEntity<PhasesInfoDto> addPhase(@PathVariable UUID project_id, @RequestBody CreatePhaseDto request){
        PhasesInfoDto body = phasesService.createPhase(project_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{phase_id}")
    public ResponseEntity<PhasesInfoDto> getPhase( @PathVariable UUID phase_id){
        PhasesInfoDto body = phasesService.getPhase( phase_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<List<PhasesInfoDto>> getPhases(@PathVariable UUID project_id){
        List<PhasesInfoDto> body= phasesService.getPhases(project_id);
        return ResponseEntity.ok(body);
    }

    @PutMapping("/{phase_id}")
    public ResponseEntity<PhasesInfoDto> updatePhase(@PathVariable UUID phase_id,
                                     @RequestBody UpdatePhaseDto request){
        PhasesInfoDto body = phasesService.updatePhase(phase_id, request);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{phase_id}")
    public ResponseEntity<?> deletePhase( @PathVariable UUID phase_id) {
        phasesService.deletePhase( phase_id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/{project_id}")
    public ResponseEntity<PhasesInfoDto> activePhase(@PathVariable UUID project_id){
        PhasesInfoDto body = phasesService.getActivePhase(project_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("overdue/{phase_id}")
    public ResponseEntity<PhaseOverdueDto> overdue(@PathVariable UUID phase_id){
        PhaseOverdueDto body = phasesService.overduePhases(phase_id);
        return ResponseEntity.ok(body);
    }
    @GetMapping("infographics/{project_id}")
    public ResponseEntity<PhasesInfographicsDto> infographics(@PathVariable UUID project_id){
        PhasesInfographicsDto body = phasesService.getInfographicForPhase(project_id);
        return ResponseEntity.ok(body);
    }
}
