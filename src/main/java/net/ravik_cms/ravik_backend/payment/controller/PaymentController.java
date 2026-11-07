package net.ravik_cms.ravik_backend.payment.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.payment.dtos.CreatePaymentDto;
import net.ravik_cms.ravik_backend.payment.dtos.PaymentInfoProjection;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import net.ravik_cms.ravik_backend.payment.dtos.UpdatePaymentDto;
import net.ravik_cms.ravik_backend.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @RequirePermission("CREATE_PAYMENT")
    @PostMapping("/{project_id}")
    public ResponseEntity<?> addPayment(@PathVariable UUID project_id, @RequestBody CreatePaymentDto request) {
        paymentService.addPayment(project_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequirePermission("READ_PAYMENT")
    @GetMapping("/{project_id}")
    public ResponseEntity<Page<PaymentInfoProjection>> getAllPayments(
            @PathVariable UUID project_id,
            @RequestParam(required = false) PaymentCategory category,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) LocalDate datePaid,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayments(project_id, category, status, datePaid, pageable));
    }

    @RequirePermission("READ_PAYMENT")
    @GetMapping("/payouts/{project_id}")
    public ResponseEntity<Page<PayoutSummaryProjection>> getAllPayouts(
            @PathVariable UUID project_id,
            @RequestParam(required = false) PaymentCategory category,
            @RequestParam(required = false) PaymentStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(paymentService.getAllPayouts(project_id, category, status, pageable));
    }

    @RequirePermission("UPDATE_PAYMENT")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updatePayment(@PathVariable UUID id, @RequestBody UpdatePaymentDto request) {
        paymentService.updatePayment(id, request);
        return ResponseEntity.ok().build();
    }

    @RequirePermission("DELETE_PAYMENT")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
