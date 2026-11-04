package net.ravik_cms.ravik_backend.reports.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.ApprovalStatus;

import java.time.LocalDate;

@Builder
public record ApprovalReportLineDto(
        Long id,
        String name,
        ApprovalStatus approvalStatus,
        LocalDate approvalDate,
        LocalDate approvalExpiry,
        Double approvalAmount,
        String registrationCode,
        String governingBody,
        boolean expiringSoon
) {
}
