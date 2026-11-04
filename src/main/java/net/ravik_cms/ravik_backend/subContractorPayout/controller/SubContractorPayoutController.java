package net.ravik_cms.ravik_backend.subContractorPayout.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.CreateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.SubContractorPayoutInfoProjection;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.UpdateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.service.SubContractorPayoutService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/subContractorPayout")
public class SubContractorPayoutController {
    private final SubContractorPayoutService subContractorPayoutService;
    private final AuthorizationService authorizationService;

    @PostMapping("/{milestone_id}")
    public ResponseEntity<List<SubContractorPayoutInfoProjection>> createSubContractorPayout(
            @PathVariable UUID milestone_id,
            @RequestBody CreateSubContractorPayoutDto request) {
        authorizationService.authorize("CREATE_PAYOUT");
        List<SubContractorPayoutInfoProjection> body = subContractorPayoutService.createSubContractorPayout(milestone_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{milestone_id}")
    public ResponseEntity<List<SubContractorPayoutInfoProjection>> getSubContractorPayout(@PathVariable UUID milestone_id) {
        authorizationService.authorize("READ_PAYOUT");
        return ResponseEntity.ok(subContractorPayoutService.getSubContractorPayout(milestone_id));
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<Page<SubContractorPayoutInfoProjection>> getByProject(
            @PathVariable UUID project_id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate jobDate,
            @PageableDefault(size = 20) Pageable pageable) {
        authorizationService.authorize("READ_PAYOUT");
        return ResponseEntity.ok(subContractorPayoutService.getByProject(project_id, search, jobDate, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateSubContractorPayout(@PathVariable UUID id, @RequestBody UpdateSubContractorPayoutDto request) {
        authorizationService.authorize("UPDATE_PAYOUT");
        subContractorPayoutService.updateSubContractorPayout(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubContractorPayout(@PathVariable UUID id) {
        authorizationService.authorize("DELETE_PAYOUT");
        subContractorPayoutService.deleteSubContractorPayout(id);
        return ResponseEntity.noContent().build();
    }
}
