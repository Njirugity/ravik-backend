package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.PlannedCostDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialRequiredService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/required")
@Tag(name = "Material Required", description = "Estimated quantity/cost of materials a milestone needs — the baseline used to compute variance and planned cost")
public class MaterialRequiredController {
    private final MaterialRequiredService materialRequiredService;
    private final UserProjectContext userProjectContext;

    @PostMapping
    @Operation(summary = "Add a material requirement to a milestone", description = "Project is derived from the milestone; project is resolved from the X-Project-ID header for authorization scoping.")
    public ResponseEntity<ApiMessageResponse> create(@RequestBody CreateMaterialRequiredDto dto) {
        materialRequiredService.create(userProjectContext.getProjectId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMessageResponse.of("Material requirement created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a material requirement by id")
    public ResponseEntity<MaterialRequiredDto> get(@Parameter(description = "Material requirement id") @PathVariable UUID id) {
        MaterialRequiredDto body = materialRequiredService.get(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/milestone/{milestoneId}")
    @Operation(summary = "List material requirements for a milestone", description = "Backs the milestone page's resources tab.")
    public ResponseEntity<List<MaterialRequiredDto>> getAllByMilestone(@Parameter(description = "Milestone id") @PathVariable UUID milestoneId) {
        List<MaterialRequiredDto> body = materialRequiredService.getAllByMilestone(userProjectContext.getProjectId(), milestoneId);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    @Operation(summary = "List all material requirements for the current project")
    public ResponseEntity<List<MaterialRequiredDto>> getAllByProject() {
        List<MaterialRequiredDto> body = materialRequiredService.getAllByProject(userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/milestone/{milestoneId}/planned-cost")
    @Operation(summary = "Total planned material cost for a milestone", description = "Sum of quantity_required * unit_price across the milestone's material requirements.")
    public ResponseEntity<PlannedCostDto> getPlannedCostForMilestone(@Parameter(description = "Milestone id") @PathVariable UUID milestoneId) {
        Double total = materialRequiredService.computeTotalPlannedCostForMilestone(userProjectContext.getProjectId(), milestoneId);
        return ResponseEntity.ok(PlannedCostDto.builder().totalPlannedCost(total).build());
    }

    @GetMapping("/planned-cost")
    @Operation(summary = "Total planned material cost for the current project", description = "Sum of quantity_required * unit_price across every material requirement in the project.")
    public ResponseEntity<PlannedCostDto> getPlannedCostForProject() {
        Double total = materialRequiredService.computeTotalPlannedCostForProject(userProjectContext.getProjectId());
        return ResponseEntity.ok(PlannedCostDto.builder().totalPlannedCost(total).build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a material requirement", description = "Null fields on the request body are left unchanged.")
    public ResponseEntity<ApiMessageResponse> update(@Parameter(description = "Material requirement id") @PathVariable UUID id, @RequestBody UpdateMaterialRequiredDto dto) {
        materialRequiredService.update(userProjectContext.getProjectId(), id, dto);
        return ResponseEntity.ok(ApiMessageResponse.of("Material requirement updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a material requirement")
    public ResponseEntity<ApiMessageResponse> delete(@Parameter(description = "Material requirement id") @PathVariable UUID id) {
        materialRequiredService.delete(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(ApiMessageResponse.of("Material requirement deleted successfully", HttpStatus.OK.value()));
    }
}
