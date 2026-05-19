package net.ravik_cms.ravik_backend.phase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/phases")
public class PhasesController {
    private final PhasesService  phasesService;

    @PostMapping("/{project_id}")
    public PhasesInfoDto addPhase(@PathVariable UUID project_id, @RequestBody CreatePhaseDto request){
        return phasesService.createPhase(project_id, request);
    }

    @GetMapping("/{phase_id}")
    public PhasesInfoDto getPhase( @PathVariable UUID phase_id){
        return phasesService.getPhase( phase_id);
    }

    @GetMapping("/all/{project_id}")
    public List<PhasesInfoDto> getPhases(@PathVariable UUID project_id){
        return phasesService.getPhases(project_id);
    }

    @PostMapping("/update/{phase_id}")
    public PhasesInfoDto updatePhase(@PathVariable UUID phase_id,
                                     @RequestBody UpdatePhaseDto request){
        return phasesService.updatePhase(phase_id, request);
    }

    @DeleteMapping("/{phase_id}")
    public void  deletePhase( @PathVariable UUID phase_id) {
        phasesService.deletePhase( phase_id);
    }

    @GetMapping("/active/{project_id}")
    public PhasesInfoDto activePhase(@PathVariable UUID project_id){
        return phasesService.getActivePhase(project_id);
    }

    @GetMapping("overdue/{phase_id}")
    public PhaseOverdueDto overdue(@PathVariable UUID phase_id){
        return phasesService.overduePhases(phase_id);
    }
    @GetMapping("infographics/{project_id}")
    public PhasesInfographicsDto infographics(@PathVariable UUID project_id){
        return phasesService.getInfographicForPhase(project_id);
    }
}
