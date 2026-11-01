package net.ravik_cms.ravik_backend.milestoneScheduling.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.ForecastService;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.ScheduleService;
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
    private final ForecastService forecastService;
    private final AuthorizationService authorizationService;

    @PostMapping("/projects/{projectId}")
    public ResponseEntity<?> calculateSchedule(@PathVariable UUID projectId){
        authorizationService.authorize("CREATE_SCHEDULE");
        scheduleService.calculateSchedule(projectId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/projects/{projectId}/forecast")
    public ResponseEntity<ForecastService.ForecastResult> recalculateForecast(@PathVariable UUID projectId){
        authorizationService.authorize("UPDATE_SCHEDULE");
        ForecastService.ForecastResult body = forecastService.recalculateForecast(projectId);
        return ResponseEntity.ok(body);
    }
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<ScheduleSummary> getSchedule(@PathVariable UUID projectId){
        authorizationService.authorize("READ_SCHEDULE");
        ScheduleSummary body = scheduleService.getScheduleSummary(projectId);
        return ResponseEntity.ok(body);
    }
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<?> deleteSchedule(@PathVariable UUID projectId){
        authorizationService.authorize("DELETE_SCHEDULE");
        scheduleService.resetSchedule(projectId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/projects/{projectId}/visualization")
    public ResponseEntity<ScheduleVisualizationResponseDto>getVisualization(@PathVariable UUID projectId){
        authorizationService.authorize("READ_SCHEDULE");
        ScheduleVisualizationResponseDto body = scheduleService.getScheduleVisualization(projectId);
        return ResponseEntity.ok(body);
    }
}
