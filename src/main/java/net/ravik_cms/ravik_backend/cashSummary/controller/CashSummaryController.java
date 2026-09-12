package net.ravik_cms.ravik_backend.cashSummary.controller;

import lombok.RequiredArgsConstructor;
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

    @GetMapping("/project/{project_id}")
    public ResponseEntity<CashSummaryDto> getCashSummary(@PathVariable UUID project_id) {
        return ResponseEntity.ok(cashSummaryService.getCashSummary(project_id));
    }
}
