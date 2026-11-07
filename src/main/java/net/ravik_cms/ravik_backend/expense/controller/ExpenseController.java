package net.ravik_cms.ravik_backend.expense.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
import net.ravik_cms.ravik_backend.expense.dtos.CreateExpenseDto;
import net.ravik_cms.ravik_backend.expense.dtos.ExpenseInfoProjection;
import net.ravik_cms.ravik_backend.expense.dtos.UpdateExpenseDto;
import net.ravik_cms.ravik_backend.expense.service.ExpenseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @RequirePermission("CREATE_EXPENSE")
    @PostMapping("/{project_id}")
    public ResponseEntity<?> addExpense(@PathVariable UUID project_id,
                                         @RequestBody CreateExpenseDto request) {
        expenseService.addExpense(request, project_id);
        return ResponseEntity.ok().build();
    }

    @RequirePermission("READ_EXPENSE")
    @GetMapping("/{project_id}")
    public ResponseEntity<Page<ExpenseInfoProjection>> getExpenses(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ExpenseInfoProjection> body = expenseService.getExpenses(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @RequirePermission("UPDATE_EXPENSE")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseDto request) {
        expenseService.updateExpense(id, request);
        return ResponseEntity.ok().build();
    }

    @RequirePermission("DELETE_EXPENSE")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable UUID id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
