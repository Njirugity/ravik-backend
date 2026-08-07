package net.ravik_cms.ravik_backend.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLabourerDto {
    @NotBlank(message = "User name required")
    private String userName;
    @NotBlank(message = "Phone number required")
    @Pattern(regexp = "^\\+[1-9][0-9\\s\\-]{7,17}$", message = "Use international format with country code, e.g. +254712345678")
    private String phoneNumber;
    @NotBlank(message = "Identification required")
    private String idNumber;
    private String roleKey;
    private Double baseDailyWage;
    private PaymentFrequency frequency;
}
