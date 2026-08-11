package net.ravik_cms.ravik_backend.approvals;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ApprovalStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateApprovalDto {
    @NotBlank(message = "Approval name cannot be blank")
    private String name;
    private ApprovalStatus approvalStatus;
    private LocalDate approvalDate;
    private LocalDate approvalExpiry;
    private Double approvalAmount;
    private String registrationCode;
    private String governingBody;
}
