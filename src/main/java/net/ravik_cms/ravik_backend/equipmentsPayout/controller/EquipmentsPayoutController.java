package net.ravik_cms.ravik_backend.equipmentsPayout.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.CreateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.EquipmentsPayoutInfoProjection;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.UpdateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.service.EquipmentsPayoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/equipmentsPayout")
public class EquipmentsPayoutController {
    private final EquipmentsPayoutService equipmentsPayoutService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<?> addEquipmentsPayout(@PathVariable UUID milestone_id,
                                                  @RequestBody CreateEquipmentsPayoutDto request) {
        equipmentsPayoutService.addEquipmentsPayout(milestone_id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<EquipmentsPayoutInfoProjection>> getEquipmentsPayout(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(equipmentsPayoutService.getEquipmentsPayout(milestone_id));
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<EquipmentsPayoutInfoProjection>> getByProject(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EquipmentCategory category,
            @RequestParam(required = false) LocalDate dateUsed,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(equipmentsPayoutService.getByProject(project_id, search, category, dateUsed, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateEquipmentsPayout(@PathVariable Long id, @RequestBody UpdateEquipmentsPayoutDto request) {
        equipmentsPayoutService.updateEquipmentsPayout(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEquipmentsPayout(@PathVariable Long id) {
        equipmentsPayoutService.deleteEquipmentsPayout(id);
        return ResponseEntity.noContent().build();
    }
}
