package net.ravik_cms.ravik_backend.expenseCategory.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.expenseCategory.dto.CreateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.dto.ExpenseCategoryInfoProjection;
import net.ravik_cms.ravik_backend.expenseCategory.dto.UpdateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.service.ExpenseCategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenseCategories")
public class ExpenseCategoryController {
    private final ExpenseCategoryService expenseCategoryService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addExpenseCategory(@PathVariable UUID project_id,
                                                 @RequestBody CreateExpenseCategoryDto request) {
        expenseCategoryService.addExpenseCategory(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<ExpenseCategoryInfoProjection>> getExpenseCategories(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ExpenseCategoryInfoProjection> body = expenseCategoryService.getExpenseCategories(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExpenseCategory(@PathVariable Long id, @RequestBody UpdateExpenseCategoryDto request) {
        expenseCategoryService.updateExpenseCategory(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpenseCategory(@PathVariable Long id) {
        expenseCategoryService.deleteExpenseCategory(id);
        return ResponseEntity.noContent().build();
    }
}
