package net.ravik_cms.ravik_backend.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.cashSummary.dtos.CashSummaryDto;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.reports.dto.ApprovalComplianceReportDto;
import net.ravik_cms.ravik_backend.reports.dto.EquipmentSubContractorPayoutReportDto;
import net.ravik_cms.ravik_backend.reports.dto.ExecutiveDashboardDto;
import net.ravik_cms.ravik_backend.reports.dto.LabourAttendanceCostReportDto;
import net.ravik_cms.ravik_backend.reports.dto.OutstandingPayablesReportDto;
import net.ravik_cms.ravik_backend.reports.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Per-project reporting endpoints, composed from existing summary services")
public class    ReportController {
    private final ReportService reportService;
    private final UserProjectContext userProjectContext;

    @GetMapping("/cash")
    @Operation(summary = "Get the cash summary for the current project", description = "Income vs payments, net cash position, outstanding cost, and funding gap.")
    public ResponseEntity<CashSummaryDto> getCashSummary() {
        return ResponseEntity.ok(reportService.getCashSummary(userProjectContext.getProjectId()));
    }

    @GetMapping("/cost")
    @Operation(summary = "Get the cost/payment summary for the current project", description = "Actual cost vs paid amount by budget category, with outstanding balance and payment progress.")
    public ResponseEntity<CostPaymentSummaryDto> getCostSummary() {
        return ResponseEntity.ok(reportService.getCostSummary(userProjectContext.getProjectId()));
    }

    @GetMapping("/account-insights")
    @Operation(summary = "Get account flows for the current project", description = "Per-account income/payment flows scoped to this project.")
    public ResponseEntity<List<ProjectAccountInsightDto>> getAccountInsights() {
        return ResponseEntity.ok(reportService.getAccountInsights(userProjectContext.getProjectId()));
    }

    @GetMapping("/approvals")
    @Operation(summary = "Get the approvals & compliance report for the current project", description = "Every approval with its compliance status, plus roll-up counts (approved/pending/rejected/expired/expiring soon within 30 days) and total approval amount.")
    public ResponseEntity<ApprovalComplianceReportDto> getApprovalsReport() {
        return ResponseEntity.ok(reportService.getApprovalsReport(userProjectContext.getProjectId()));
    }

    @GetMapping("/outstanding-payables")
    @Operation(summary = "Get the outstanding payables report for the current project", description = "Every not-fully-paid labour, equipment, subcontractor, or expense payout, ranked by outstanding amount, plus a per-category roll-up.")
    public ResponseEntity<OutstandingPayablesReportDto> getOutstandingPayablesReport() {
        return ResponseEntity.ok(reportService.getOutstandingPayablesReport(userProjectContext.getProjectId()));
    }

    @GetMapping("/labour-attendance")
    @Operation(summary = "Get the labour & attendance cost rollup for the current project", description = "Per-job-title headcount, days present/absent, and gross pay earned, plus project-wide labour payout totals (invoiced, paid, outstanding).")
    public ResponseEntity<LabourAttendanceCostReportDto> getLabourAttendanceCostReport() {
        return ResponseEntity.ok(reportService.getLabourAttendanceCostReport(userProjectContext.getProjectId()));
    }

    @GetMapping("/equipment-subcontractor")
    @Operation(summary = "Get the equipment & subcontractor payout summary for the current project", description = "Planned vs actual cost for every equipment and subcontractor requirement, matched by requirement, plus project totals.")
    public ResponseEntity<EquipmentSubContractorPayoutReportDto> getEquipmentSubContractorPayoutReport() {
        return ResponseEntity.ok(reportService.getEquipmentSubContractorPayoutReport(userProjectContext.getProjectId()));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get the executive dashboard for the current project", description = "Top-line project health: budget utilization, cash position, schedule/milestone progress, and outstanding risk (unpaid bills, expiring approvals, unpaid labour).")
    public ResponseEntity<ExecutiveDashboardDto> getExecutiveDashboard() {
        return ResponseEntity.ok(reportService.getExecutiveDashboard(userProjectContext.getProjectId()));
    }
}
