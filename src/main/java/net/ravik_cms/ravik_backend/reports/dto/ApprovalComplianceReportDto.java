package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ApprovalComplianceReportDto(
        List<ApprovalReportLineDto> approvals,
        long totalCount,
        long approvedCount,
        long pendingCount,
        long rejectedCount,
        long expiredCount,
        long expiringSoonCount,
        double totalApprovalAmount
) {
}
