package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.materials.dto.MaterialsReportDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialsReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/report")
@Tag(name = "Materials Report", description = "The materials page aggregate: every material type in the project with planned vs actual quantity/cost and variances")
public class MaterialsReportController {
    private final MaterialsReportService materialsReportService;
    private final UserProjectContext userProjectContext;

    @GetMapping
    @Operation(summary = "Get the materials page report for the current project", description = "One row per material type: required/delivered/used quantities and costs, balance (delivered - used), and variances (used vs required, delivered cost vs required cost).")
    public ResponseEntity<MaterialsReportDto> getProjectReport() {
        MaterialsReportDto body = materialsReportService.getProjectReport(userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }
}
