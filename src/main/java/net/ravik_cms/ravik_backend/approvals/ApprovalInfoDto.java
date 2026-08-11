package net.ravik_cms.ravik_backend.approvals;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ApprovalStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalInfoDto {
    private Long id;
    private String name;
    private ApprovalStatus approvalStatus;
    private LocalDate approvalDate;
    private LocalDate approvalExpiry;
    private Double approvalAmount;
    private String registrationCode;
    private String governingBody;
    private UUID projectId;
}
