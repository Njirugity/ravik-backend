package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.materials.dto.MaterialAtRiskDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialAlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/alerts")
@Tag(name = "Material Alerts", description = "Milestone-almost-due material shortfall alerts: required vs in-store stock for milestones nearing their finish date")
public class MaterialAlertController {
    private final MaterialAlertService materialAlertService;
    private final UserProjectContext userProjectContext;

    @GetMapping("/at-risk")
    @Operation(summary = "List material shortfalls on milestones due soon", description = "For every not-yet-completed milestone whose earliest finish date falls within daysThreshold days, " +
            "checks each required material against project-wide in-store stock (delivered minus used) and returns the ones falling short, with the additional quantity needed.")
    public ResponseEntity<List<MaterialAtRiskDto>> getAtRiskMaterials(
            @Parameter(description = "How many days out from today counts as \"almost due\"") @RequestParam(defaultValue = "7") int daysThreshold
    ) {
        List<MaterialAtRiskDto> body = materialAlertService.getAtRiskMaterials(userProjectContext.getProjectId(), daysThreshold);
        return ResponseEntity.ok(body);
    }
}
