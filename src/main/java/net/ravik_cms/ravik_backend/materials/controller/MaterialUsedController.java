package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialStockDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedCreationResult;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialUsedService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/used")
@Tag(name = "Material Used", description = "Materials actually consumed on site for a milestone, deducted from delivered stock")
public class MaterialUsedController {
    private final MaterialUsedService materialUsedService;
    private final UserProjectContext userProjectContext;

    @PostMapping
    @Operation(summary = "Record material usage for a milestone", description = "Deducts from the referenced delivery's material stock. If this pushes total used past total delivered," +
            "    the entry is still saved and the response message flags it — this is a soft warning, not a hard block.")
    public ResponseEntity<ApiMessageResponse> create(@RequestBody CreateMaterialUsedDto dto) {
        MaterialUsedCreationResult result = materialUsedService.create(userProjectContext.getProjectId(), dto);
        String message = result.stockExceeded()
                ? String.format(Locale.ROOT, "Material usage recorded successfully. Warning: exceeds delivered stock by %.2f", -result.remainingStock())
                : "Material usage recorded successfully";
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMessageResponse.of(message, HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a material usage record by id")
    public ResponseEntity<MaterialUsedDto> get(@Parameter(description = "Material usage record id") @PathVariable UUID id) {
        MaterialUsedDto body = materialUsedService.get(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/milestone/{milestoneId}")
    @Operation(summary = "List material usage for a milestone", description = "Backs the milestone page's resources tab.")
    public ResponseEntity<List<MaterialUsedDto>> getAllByMilestone(@Parameter(description = "Milestone id") @PathVariable UUID milestoneId) {
        List<MaterialUsedDto> body = materialUsedService.getAllByMilestone(userProjectContext.getProjectId(), milestoneId);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    @Operation(summary = "List all material usage for the current project")
    public ResponseEntity<List<MaterialUsedDto>> getAllByProject() {
        List<MaterialUsedDto> body = materialUsedService.getAllByProject(userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/material/{materialListId}/stock")
    @Operation(summary = "Delivered vs used vs remaining for a material type", description = "The quantity-in-store view: total delivered minus total used.")
    public ResponseEntity<MaterialStockDto> getStock(@Parameter(description = "Material type id") @PathVariable UUID materialListId) {
        MaterialStockDto body = materialUsedService.computeStock(userProjectContext.getProjectId(), materialListId);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a material usage record", description = "Null fields on the request body are left unchanged. Does not re-run the stock warning check.")
    public ResponseEntity<ApiMessageResponse> update(@Parameter(description = "Material usage record id") @PathVariable UUID id, @RequestBody UpdateMaterialUsedDto dto) {
        materialUsedService.update(userProjectContext.getProjectId(), id, dto);
        return ResponseEntity.ok(ApiMessageResponse.of("Material usage updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a material usage record")
    public ResponseEntity<ApiMessageResponse> delete(@Parameter(description = "Material usage record id") @PathVariable UUID id) {
        materialUsedService.delete(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(ApiMessageResponse.of("Material usage deleted successfully", HttpStatus.OK.value()));
    }
}
