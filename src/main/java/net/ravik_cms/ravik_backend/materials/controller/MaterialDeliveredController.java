package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.DeliveredStockDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialDeliveredService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/delivered")
@Tag(name = "Material Delivered", description = "Materials delivered to the site. Project-scoped, not milestone-scoped — material_used deducts stock from these deliveries")
public class MaterialDeliveredController {
    private final MaterialDeliveredService materialDeliveredService;
    private final UserProjectContext userProjectContext;

    @PostMapping
    @Operation(summary = "Record a material delivery", description = "Project is resolved from the X-Project-ID header.")
    public ResponseEntity<ApiMessageResponse> create(@RequestBody CreateMaterialDeliveredDto dto) {
        materialDeliveredService.create(userProjectContext.getProjectId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMessageResponse.of("Material delivery recorded successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a material delivery by id")
    public ResponseEntity<MaterialDeliveredDto> get(@Parameter(description = "Material delivery id") @PathVariable UUID id) {
        MaterialDeliveredDto body = materialDeliveredService.get(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    @Operation(summary = "List all material deliveries for the current project")
    public ResponseEntity<List<MaterialDeliveredDto>> getAllByProject() {
        List<MaterialDeliveredDto> body = materialDeliveredService.getAllByProject(userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/material/{materialListId}")
    @Operation(summary = "List delivery history for a specific material type")
    public ResponseEntity<List<MaterialDeliveredDto>> getAllByMaterial(@Parameter(description = "Material type id") @PathVariable UUID materialListId) {
        List<MaterialDeliveredDto> body = materialDeliveredService.getAllByMaterial(userProjectContext.getProjectId(), materialListId);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/material/{materialListId}/total-delivered")
    @Operation(summary = "Total quantity delivered for a material type", description = "Sum of quantity_delivered across the material's (non-deleted) deliveries — the quantity in store before usage is deducted.")
    public ResponseEntity<DeliveredStockDto> getTotalDelivered(@Parameter(description = "Material type id") @PathVariable UUID materialListId) {
        Double total = materialDeliveredService.computeTotalDelivered(userProjectContext.getProjectId(), materialListId);
        return ResponseEntity.ok(DeliveredStockDto.builder().totalDelivered(total).build());
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a material delivery", description = "Null fields on the request body are left unchanged.")
    public ResponseEntity<ApiMessageResponse> update(@Parameter(description = "Material delivery id") @PathVariable UUID id, @RequestBody UpdateMaterialDeliveredDto dto) {
        materialDeliveredService.update(userProjectContext.getProjectId(), id, dto);
        return ResponseEntity.ok(ApiMessageResponse.of("Material delivery updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a material delivery")
    public ResponseEntity<ApiMessageResponse> delete(@Parameter(description = "Material delivery id") @PathVariable UUID id) {
        materialDeliveredService.delete(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(ApiMessageResponse.of("Material delivery deleted successfully", HttpStatus.OK.value()));
    }
}
