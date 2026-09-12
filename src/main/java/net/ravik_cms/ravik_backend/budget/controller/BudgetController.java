package net.ravik_cms.ravik_backend.budget.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budget.dtos.BudgetInfoProjection;
import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;
import net.ravik_cms.ravik_backend.budget.dtos.UpdateBudgetDto;
import net.ravik_cms.ravik_backend.budget.service.BudgetService;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addBudget(@PathVariable UUID project_id,
                                        @RequestBody CreateBudgetDto request) {
        budgetService.addBudget(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<BudgetInfoProjection>> getBudgets(
            @PathVariable UUID project_id,
            @RequestParam(required = false) BudgetCategory category,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<BudgetInfoProjection> body = budgetService.getBudgets(project_id, category, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable UUID id, @RequestBody UpdateBudgetDto request) {
        budgetService.updateBudget(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable UUID id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }
}
