package net.ravik_cms.ravik_backend.costSummary.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.costSummary.service.CostSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/costSummary")
public class CostSummaryController {
    private final CostSummaryService costSummaryService;

    @GetMapping("/project/{project_id}")
    public ResponseEntity<CostPaymentSummaryDto> getCostPaymentSummary(@PathVariable UUID project_id) {
        return ResponseEntity.ok(costSummaryService.getCostPaymentSummary(project_id));
    }
}
