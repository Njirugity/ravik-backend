package net.ravik_cms.ravik_backend.expense.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
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
    private final AuthorizationService authorizationService;

    @PostMapping("/{project_id}")
    public ResponseEntity<?> addExpense(@PathVariable UUID project_id,
                                         @RequestBody CreateExpenseDto request) {
        authorizationService.authorize("CREATE_EXPENSE");
        expenseService.addExpense(request, project_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{project_id}")
    public ResponseEntity<Page<ExpenseInfoProjection>> getExpenses(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        authorizationService.authorize("READ_EXPENSE");
        Page<ExpenseInfoProjection> body = expenseService.getExpenses(project_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable UUID id, @RequestBody UpdateExpenseDto request) {
        authorizationService.authorize("UPDATE_EXPENSE");
        expenseService.updateExpense(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable UUID id) {
        authorizationService.authorize("DELETE_EXPENSE");
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
