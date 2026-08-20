package net.ravik_cms.ravik_backend.milestoneBudget.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.CreateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.MilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.MilestoneBudgetSummaryDto;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.UpdateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.service.MilestoneBudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/milestoneBudgets")
public class MilestoneBudgetController {
    private final MilestoneBudgetService milestoneBudgetService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<List<MilestoneBudgetLineDto>> createBudgetLines(
            @PathVariable UUID milestone_id,
            @RequestBody List<CreateMilestoneBudgetLineDto> request) {
        List<MilestoneBudgetLineDto> body = milestoneBudgetService.createBudgetLines(milestone_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<MilestoneBudgetLineDto>> getBudgetLines(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(milestoneBudgetService.getBudgetLines(milestone_id));
    }

    @GetMapping("/{milestone_id}/summary")
    public ResponseEntity<MilestoneBudgetSummaryDto> getBudgetSummary(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(milestoneBudgetService.getBudgetSummary(milestone_id));
    }

    @PutMapping("/line/{id}")
    public ResponseEntity<MilestoneBudgetLineDto> updateBudgetLine(
            @PathVariable Long id,
            @RequestBody UpdateMilestoneBudgetLineDto request) {
        return ResponseEntity.ok(milestoneBudgetService.updateBudgetLine(id, request));
    }

    @DeleteMapping("/line/{id}")
    public ResponseEntity<?> deleteBudgetLine(@PathVariable Long id) {
        milestoneBudgetService.deleteBudgetLine(id);
        return ResponseEntity.noContent().build();
    }
}
