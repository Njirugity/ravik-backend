package net.ravik_cms.ravik_backend.accountInsights.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.service.AccountAccessGuard;
import net.ravik_cms.ravik_backend.common.dataInitializer.DefaultPermissions;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
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
    private final AccountAccessGuard accountAccessGuard;

    /** Balances across all of the client's projects; only roles the client granted can see them. */
    @RequirePermission(DefaultPermissions.VIEW_CLIENT_ACCOUNT_SUMMARY)
    @GetMapping("/client/{client_id}")
    public ResponseEntity<List<ClientAccountInsightDto>> getClientDashboard(@PathVariable UUID client_id) {
        accountAccessGuard.requireCurrentClient(client_id);
        return ResponseEntity.ok(accountInsightsService.getClientDashboard(client_id));
    }

    @RequirePermission("READ_ACCOUNT")
    @GetMapping("/project/{project_id}")
    public ResponseEntity<List<ProjectAccountInsightDto>> getProjectAccountFlows(@PathVariable UUID project_id) {
        accountAccessGuard.requireCurrentProject(project_id);
        return ResponseEntity.ok(accountInsightsService.getProjectAccountFlows(project_id));
    }

    /** Every account the client owns, with this project's figures (zero where unused here). */
    @RequirePermission("READ_ACCOUNT")
    @GetMapping("/project/{project_id}/accounts")
    public ResponseEntity<List<ProjectAccountInsightDto>> getClientAccountsForProject(@PathVariable UUID project_id) {
        accountAccessGuard.requireCurrentProject(project_id);
        return ResponseEntity.ok(accountInsightsService.getClientAccountsForProject(project_id));
    }
}
