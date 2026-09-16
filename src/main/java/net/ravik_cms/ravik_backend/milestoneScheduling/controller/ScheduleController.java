package net.ravik_cms.ravik_backend.milestoneScheduling;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.ScheduleSummary;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.ScheduleVisualizationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<?> calculateSchedule(@PathVariable UUID projectId){
        scheduleService.calculateSchedule(projectId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ScheduleSummary> getSchedule(@PathVariable UUID projectId){
        ScheduleSummary body = scheduleService.getScheduleSummary(projectId);
        return ResponseEntity.ok(body);
    }
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable UUID projectId){
        scheduleService.resetSchedule(projectId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/projects/{projectId}/visualization")
    public ResponseEntity<ScheduleVisualizationResponseDto>getVisualization(@PathVariable UUID projectId){
        ScheduleVisualizationResponseDto body = scheduleService.getScheduleVisualization(projectId);
        return ResponseEntity.ok(body);
    }
}
