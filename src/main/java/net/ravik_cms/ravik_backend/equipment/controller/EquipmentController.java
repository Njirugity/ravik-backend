package net.ravik_cms.ravik_backend.equipment.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.equipment.dtos.EquipmentInfoProjection;
import net.ravik_cms.ravik_backend.equipment.service.EquipmentService;
import net.ravik_cms.ravik_backend.equipment.dtos.UpdateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.dtos.CreateEquipmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipment")
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addEquipment(@PathVariable UUID project_id,
                                           @RequestBody CreateEquipmentDto request) {
        equipmentService.addEquipment(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<EquipmentInfoProjection>> getEquipment(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EquipmentCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<EquipmentInfoProjection> body = equipmentService.getEquipment(project_id, search, category, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEquipment(@PathVariable Long id, @RequestBody UpdateEquipmentDto request) {
        equipmentService.updateEquipment(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
