package net.ravik_cms.ravik_backend.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    @NotBlank(message = "User name required")
    private String userName;
    @Email(message = "Email is not valid")
    private String email;
    private String password;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9][0-9\\s\\-]{7,17}$", message = "Use international format with country code, e.g. +254712345678")
    private String phoneNumber;
    @NotBlank(message = "Identification required")
    private String idNumber;
    private boolean hasSystemAccess;
    private UUID roleId;
    private Long jobTitleId;
    private Double baseWage;
    private PaymentFrequency frequency;
    private boolean updateJobTitle;
    private boolean generateAttendance;
    private StaffStatus status;
}
