package net.ravik_cms.ravik_backend.reports.service;

import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.cashSummary.dtos.CashSummaryDto;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.reports.dto.ApprovalComplianceReportDto;
import net.ravik_cms.ravik_backend.reports.dto.EquipmentSubContractorPayoutReportDto;
import net.ravik_cms.ravik_backend.reports.dto.ExecutiveDashboardDto;
import net.ravik_cms.ravik_backend.reports.dto.LabourAttendanceCostReportDto;
import net.ravik_cms.ravik_backend.reports.dto.OutstandingPayablesReportDto;

import java.util.List;
import java.util.UUID;

public interface ReportService {
    CashSummaryDto getCashSummary(UUID projectId);

    CostPaymentSummaryDto getCostSummary(UUID projectId);

    List<ProjectAccountInsightDto> getAccountInsights(UUID projectId);

    ApprovalComplianceReportDto getApprovalsReport(UUID projectId);

    OutstandingPayablesReportDto getOutstandingPayablesReport(UUID projectId);

    LabourAttendanceCostReportDto getLabourAttendanceCostReport(UUID projectId);

    EquipmentSubContractorPayoutReportDto getEquipmentSubContractorPayoutReport(UUID projectId);

    ExecutiveDashboardDto getExecutiveDashboard(UUID projectId);
}
