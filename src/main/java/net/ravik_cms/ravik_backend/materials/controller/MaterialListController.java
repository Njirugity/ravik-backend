package net.ravik_cms.ravik_backend.materials.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.common.response.ApiMessageResponse;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.service.MaterialListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/materials/list")
@Tag(name = "Material List", description = "Project-scoped catalogue of material types (name + metric) used by material_required, material_delivered and material_used")
public class MaterialListController {
    private final MaterialListService materialListService;
    private final UserProjectContext userProjectContext;

    @PostMapping
    @Operation(summary = "Create a material type", description = "Adds a new material type to the current project's catalogue. Project is resolved from the X-Project-ID header.")
    public ResponseEntity<ApiMessageResponse> create(@RequestBody CreateMaterialListDto dto) {
        materialListService.create(userProjectContext.getProjectId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiMessageResponse.of("Material created successfully", HttpStatus.CREATED.value()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a material type by id")
    public ResponseEntity<MaterialListDto> get(@Parameter(description = "Material type id") @PathVariable UUID id) {
        MaterialListDto body = materialListService.get(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(body);
    }

    @GetMapping
    @Operation(summary = "List all material types for the current project")
    public ResponseEntity<List<MaterialListDto>> getAll() {
        List<MaterialListDto> body = materialListService.getAll(userProjectContext.getProjectId());
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a material type", description = "Null fields on the request body are left unchanged.")
    public ResponseEntity<ApiMessageResponse> update(@Parameter(description = "Material type id") @PathVariable UUID id, @RequestBody UpdateMaterialListDto dto) {
        materialListService.update(userProjectContext.getProjectId(), id, dto);
        return ResponseEntity.ok(ApiMessageResponse.of("Material updated successfully", HttpStatus.OK.value()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a material type")
    public ResponseEntity<ApiMessageResponse> delete(@Parameter(description = "Material type id") @PathVariable UUID id) {
        materialListService.delete(userProjectContext.getProjectId(), id);
        return ResponseEntity.ok(ApiMessageResponse.of("Material deleted successfully", HttpStatus.OK.value()));
    }
}
