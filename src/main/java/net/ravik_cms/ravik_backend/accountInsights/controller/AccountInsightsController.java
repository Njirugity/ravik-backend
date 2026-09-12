package net.ravik_cms.ravik_backend.accountInsights.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ClientAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.service.AccountInsightsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accountInsights")
public class AccountInsightsController {
    private final AccountInsightsService accountInsightsService;

    @GetMapping("/client/{client_id}")
    public ResponseEntity<List<ClientAccountInsightDto>> getClientDashboard(@PathVariable UUID client_id) {
        return ResponseEntity.ok(accountInsightsService.getClientDashboard(client_id));
    }

    @GetMapping("/project/{project_id}")
    public ResponseEntity<List<ProjectAccountInsightDto>> getProjectAccountFlows(@PathVariable UUID project_id) {
        return ResponseEntity.ok(accountInsightsService.getProjectAccountFlows(project_id));
    }
}
