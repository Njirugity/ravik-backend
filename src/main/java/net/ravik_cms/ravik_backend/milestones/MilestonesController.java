package net.ravik_cms.ravik_backend.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/milestones")
@RequiredArgsConstructor
public class MilestonesController {
    private final MilestonesService milestonesService;

    @PostMapping("/{phase_id}")
    public ResponseEntity<MilestoneInfoDto> addMilestone(@PathVariable UUID phase_id, @RequestBody CreateMilestoneDto dto){
        MilestoneInfoDto body = milestonesService.createMilestone(phase_id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<MilestoneInfoDto> getMilestone(@PathVariable UUID milestone_id){
        MilestoneInfoDto body = milestonesService.getMilestone(milestone_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/phase/{phase_id}")
    public ResponseEntity<List<MilestoneInfoDto>> getPhaseMilestones(@PathVariable UUID phase_id){
        List<MilestoneInfoDto> body = milestonesService.getPhaseMilestones(phase_id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<List<MilestoneInfoDto>> getProjectMilestone(@PathVariable UUID project_id){
        List<MilestoneInfoDto> body = milestonesService.getProjectMilestones(project_id);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/update/{milestone_id}")
    public ResponseEntity<MilestoneInfoDto> updateMilestone(@PathVariable UUID milestone_id, @RequestBody UpdateMilestoneDto dto){
        MilestoneInfoDto body = milestonesService.updateMilestone(milestone_id, dto);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{milestone_id}")
    public ResponseEntity<?> deleteMilestone(@PathVariable UUID milestone_id){
        milestonesService.deleteMilestone(milestone_id);
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/overdue/{project_id}")
//    public ResponseEntity<List<TimeVarianceDto>> overdueMilestones (@PathVariable UUID project_id){
//        List<TimeVarianceDto> body = milestonesService.getTimeVariances(project_id, LocalDate.now());
//        return ResponseEntity.ok(body);
//    }
    @GetMapping("/eligible/{project_id}")
    public ResponseEntity<List<PossibleActiveMilestonesDto>> getEligibleMilestone(@PathVariable UUID project_id){
        List<PossibleActiveMilestonesDto> body = milestonesService.getEligibleMilestones(project_id);
        return ResponseEntity.ok(body);
    }


}
