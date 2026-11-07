package net.ravik_cms.ravik_backend.labourPayout.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.labourPayout.dtos.CreateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.dtos.LabourPayoutInfoProjection;
import net.ravik_cms.ravik_backend.labourPayout.dtos.UpdateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.service.LabourPayoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/labourPayouts")
public class LabourPayoutController {
    private final LabourPayoutService labourPayoutService;

    @RequirePermission("CREATE_PAYOUT")
    @PostMapping("/{project_id}")
    public ResponseEntity<?> addLabourPayout(@PathVariable UUID project_id,
                                              @RequestBody CreateLabourPayoutDto request) {
        labourPayoutService.addLabourPayout(request, project_id);
        return ResponseEntity.ok().build();
    }

    @RequirePermission("READ_PAYOUT")
    @GetMapping("/{project_id}")
    public ResponseEntity<Page<LabourPayoutInfoProjection>> getLabourPayouts(
            @PathVariable UUID project_id,
            @RequestParam(required = false) PaymentStatus paymentStatus,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LabourPayoutInfoProjection> body = labourPayoutService.getLabourPayouts(project_id, paymentStatus, pageable);
        return ResponseEntity.ok(body);
    }

    @RequirePermission("UPDATE_PAYOUT")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateLabourPayout(@PathVariable UUID id, @RequestBody UpdateLabourPayoutDto request) {
        labourPayoutService.updateLabourPayout(id, request);
        return ResponseEntity.ok().build();
    }

    @RequirePermission("DELETE_PAYOUT")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLabourPayout(@PathVariable UUID id) {
        labourPayoutService.deleteLabourPayout(id);
        return ResponseEntity.noContent().build();
    }
}
