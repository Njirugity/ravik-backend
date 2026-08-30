package net.ravik_cms.ravik_backend.income.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.income.dtos.CreateIncomeDto;
import net.ravik_cms.ravik_backend.income.dtos.IncomeInfoProjection;
import net.ravik_cms.ravik_backend.income.dtos.UpdateIncomeDto;
import net.ravik_cms.ravik_backend.income.service.IncomeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addIncome(@PathVariable UUID project_id,
                                        @RequestBody CreateIncomeDto request) {
        incomeService.addIncome(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<IncomeInfoProjection>> getIncomes(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<IncomeInfoProjection> body = incomeService.getIncomes(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable UUID id, @RequestBody UpdateIncomeDto request) {
        incomeService.updateIncome(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable UUID id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
