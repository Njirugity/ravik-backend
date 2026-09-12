package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.CreateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.EquipmentRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.UpdateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.service.EquipmentRequiredService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipmentRequired")
public class EquipmentRequiredController {
    private final EquipmentRequiredService equipmentRequiredService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<List<EquipmentRequiredInfoProjection>> createEquipmentRequired(
            @PathVariable UUID milestone_id,
            @RequestBody List<CreateEquipmentRequiredDto> request) {
        List<EquipmentRequiredInfoProjection> body = equipmentRequiredService.createEquipmentRequired(milestone_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}/milestone")
    public ResponseEntity<List<EquipmentRequiredInfoProjection>> getEquipmentRequired(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(equipmentRequiredService.getEquipmentRequired(milestone_id));
    }
    @GetMapping("/{project_id}")
    public ResponseEntity<Page<EquipmentRequiredInfoProjection>> getEquipmentRequiredByProject(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false)EquipmentCategory category,
            @PageableDefault(size = 20) Pageable pageable
            ){
        return ResponseEntity.ok(equipmentRequiredService.getEquipmentRequiredBYProject(
                project_id, search, category, pageable));    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEquipmentRequired(@PathVariable Long id, @RequestBody UpdateEquipmentRequiredDto request) {
        equipmentRequiredService.updateEquipmentRequired(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipmentRequired(@PathVariable Long id) {
        equipmentRequiredService.deleteEquipmentRequired(id);
        return ResponseEntity.noContent().build();
    }
}
