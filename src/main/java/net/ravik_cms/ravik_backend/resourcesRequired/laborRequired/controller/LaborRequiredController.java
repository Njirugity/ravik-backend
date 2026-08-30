package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.CreateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.LaborRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.UpdateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.service.LaborRequiredService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/laborRequired")
public class LaborRequiredController {
    private final LaborRequiredService laborRequiredService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<List<LaborRequiredInfoProjection>> createLaborRequired(
            @PathVariable UUID milestone_id,
            @RequestBody List<CreateLaborRequiredDto> request) {
        List<LaborRequiredInfoProjection> body = laborRequiredService.createLaborRequired(milestone_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<LaborRequiredInfoProjection>> getLaborRequired(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(laborRequiredService.getLaborRequired(milestone_id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLaborRequired(@PathVariable Long id, @RequestBody UpdateLaborRequiredDto request) {
        laborRequiredService.updateLaborRequired(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaborRequired(@PathVariable Long id) {
        laborRequiredService.deleteLaborRequired(id);
        return ResponseEntity.noContent().build();
    }
}
