package net.ravik_cms.ravik_backend.budgetSummary.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.MilestoneBudgetVsActualDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.PhaseBudgetSummaryDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.ProjectBudgetSummaryDto;
import net.ravik_cms.ravik_backend.budgetSummary.service.BudgetSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budgetSummary")
public class BudgetSummaryController {
    private final BudgetSummaryService budgetSummaryService;

    @GetMapping("/project/{project_id}")
    public ResponseEntity<ProjectBudgetSummaryDto> getProjectBudgetSummary(@PathVariable UUID project_id) {
        return ResponseEntity.ok(budgetSummaryService.getProjectBudgetSummary(project_id));
    }

    @GetMapping("/phase/{phase_id}")
    public ResponseEntity<PhaseBudgetSummaryDto> getPhaseBudgetSummary(@PathVariable UUID phase_id) {
        return ResponseEntity.ok(budgetSummaryService.getPhaseBudgetSummary(phase_id));
    }

    @GetMapping("/milestone/{milestone_id}")
    public ResponseEntity<MilestoneBudgetVsActualDto> getMilestoneBudgetVsActual(@PathVariable UUID milestone_id) {
        return ResponseEntity.ok(budgetSummaryService.getMilestoneBudgetVsActual(milestone_id));
    }
}
