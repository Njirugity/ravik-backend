package net.ravik_cms.ravik_backend.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private UUID id;
    private String userName;
    private String email;
    private String phoneNumber;
    private String idNumber;
    private Long memberId;
    private UUID roleId;
    private String roleName;
    private Long jobTitleId;
    private String jobTitleName;
    private Double baseWage;
    private PaymentFrequency frequency;
    private boolean generateAttendance;
    private StaffStatus status;
}
