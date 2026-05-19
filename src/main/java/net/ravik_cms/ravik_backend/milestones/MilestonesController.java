package net.ravik_cms.ravik_backend.milestones;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/milestones")
@RequiredArgsConstructor
public class MilestonesController {
    private final MilestonesService milestonesService;

    @PostMapping("/{phase_id}")
    public MilestoneInfoDto addMilestone(@PathVariable UUID phase_id, @RequestBody CreateMilestoneDto dto){
        return milestonesService.createMilestone(phase_id, dto);
    }

    @GetMapping("/{milestone_id}")
    public MilestoneInfoDto getMilestone(@PathVariable UUID milestone_id){
        return milestonesService.getMilestone(milestone_id);
    }

    @GetMapping("/phase_milestone/{phase_id}")
    public List<MilestoneInfoDto> getPhaseMilestones(@PathVariable UUID phase_id){
        return milestonesService.getPhaseMilestones(phase_id);
    }

    @GetMapping("/project_milestone/{project_id}")
    public List<MilestoneInfoDto> getProjectMilestone(@PathVariable UUID project_id){
        return milestonesService.getProjectMilestones(project_id);
    }

    @PostMapping("/update/{milestone_id}")
    public MilestoneInfoDto updateMilestone(@PathVariable UUID milestone_id, @RequestBody UpdateMilestoneDto dto){
        return milestonesService.updateMilestone(milestone_id, dto);
    }

    @DeleteMapping("/{milestone_id}")
    public void deleteMilestone(@PathVariable UUID milestone_id){
        milestonesService.deleteMilestone(milestone_id);
    }

    @GetMapping("/overdue/{project_id}")
    public List<TimeVarianceDto> overdueMilestones (@PathVariable UUID project_id){
        return milestonesService.getTimeVariances(project_id, LocalDate.now());
    }

}
