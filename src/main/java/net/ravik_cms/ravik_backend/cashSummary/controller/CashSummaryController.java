package net.ravik_cms.ravik_backend.cashSummary.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.cashSummary.dtos.CashSummaryDto;
import net.ravik_cms.ravik_backend.cashSummary.service.CashSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cashSummary")
public class CashSummaryController {
    private final CashSummaryService cashSummaryService;
    private final AuthorizationService authorizationService;

    @GetMapping("/project/{project_id}")
    public ResponseEntity<CashSummaryDto> getCashSummary(@PathVariable UUID project_id) {
        authorizationService.authorize("READ_PAYMENT");
        return ResponseEntity.ok(cashSummaryService.getCashSummary(project_id));
    }
}
