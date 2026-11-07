package net.ravik_cms.ravik_backend.dashboard.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.RequirePermission;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.dashboard.dto.AccountsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.DashboardOverviewDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MaterialsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MilestoneDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PaymentsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.ScheduleDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Project analytics across schedule, milestones, budget, payments, accounts and materials. Project is resolved from the X-Project-ID header.")
public class DashboardController {
    private static final int MAX_UPCOMING_DAYS = 90;
    private static final int MAX_TREND_MONTHS = 24;

    private final DashboardService dashboardService;
    private final UserProjectContext userProjectContext;

    @RequirePermission("READ_PROJECT")
    @GetMapping
    @Operation(summary = "Dashboard overview", description = "Headline KPI tiles: progress, schedule variance, budget used, cash position, milestone and material risk counts.")
    public ResponseEntity<DashboardOverviewDto> getOverview() {
        return ResponseEntity.ok(dashboardService.getOverview(userProjectContext.getProjectId()));
    }

    @RequirePermission("READ_SCHEDULE")
    @GetMapping("/schedule")
    @Operation(summary = "Schedule analytics", description = "Baseline vs forecast finish, variance in working days, % complete vs time elapsed, and critical/near-critical counts.")
    public ResponseEntity<ScheduleDashboardDto> getSchedule() {
        return ResponseEntity.ok(dashboardService.getSchedule(userProjectContext.getProjectId()));
    }

    @RequirePermission("READ_MILESTONE")
    @GetMapping("/milestones")
    @Operation(summary = "Milestone analytics", description = "Status and date-status counts, overdue and upcoming milestones, and per-phase progress.")
    public ResponseEntity<MilestoneDashboardDto> getMilestones(
            @Parameter(description = "Window for upcoming milestones, in days (1-90)")
            @RequestParam(defaultValue = "14") int upcomingDays) {
        return ResponseEntity.ok(dashboardService.getMilestones(userProjectContext.getProjectId(),
                Math.clamp(upcomingDays, 1, MAX_UPCOMING_DAYS)));
    }

    @RequirePermission("READ_BUDGET")
    @GetMapping("/budget")
    @Operation(summary = "Budget analytics", description = "Approved vs allocated vs actual per budget category, and the milestones overrunning their budget most.")
    public ResponseEntity<BudgetDashboardDto> getBudget() {
        return ResponseEntity.ok(dashboardService.getBudget(userProjectContext.getProjectId()));
    }

    @RequirePermission("READ_PAYMENT")
    @GetMapping("/payments")
    @Operation(summary = "Payment analytics", description = "Cash position, paid vs cost per category, payables by status and the monthly income/payment trend.")
    public ResponseEntity<PaymentsDashboardDto> getPayments(
            @Parameter(description = "Number of months in the trend, current month included (1-24)")
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(dashboardService.getPayments(userProjectContext.getProjectId(),
                Math.clamp(months, 1, MAX_TREND_MONTHS)));
    }

    @RequirePermission("READ_ACCOUNT")
    @GetMapping("/accounts")
    @Operation(summary = "Account analytics", description = "Income and payments per account on this project, with each account's overall balance.")
    public ResponseEntity<AccountsDashboardDto> getAccounts() {
        return ResponseEntity.ok(dashboardService.getAccounts(userProjectContext.getProjectId()));
    }

    @RequirePermission({"READ_MATERIALS_REPORT", "READ_MATERIAL_ALERTS"})
    @GetMapping("/materials")
    @Operation(summary = "Material analytics", description = "Planned vs delivered material cost, stock shortfalls against remaining requirements, and materials at risk for milestones due within 7 days.")
    public ResponseEntity<MaterialsDashboardDto> getMaterials() {
        return ResponseEntity.ok(dashboardService.getMaterials(userProjectContext.getProjectId()));
    }
}
