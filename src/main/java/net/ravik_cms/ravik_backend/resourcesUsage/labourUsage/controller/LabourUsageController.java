package net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.dtos.LabourComparisonDto;
import net.ravik_cms.ravik_backend.resourcesUsage.labourUsage.service.LabourUsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/labourUsage")
public class LabourUsageController {
    private final LabourUsageService labourUsageService;

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<LabourComparisonDto>> getLabourComparison(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(labourUsageService.getLabourComparison(milestone_id));
    }
}
