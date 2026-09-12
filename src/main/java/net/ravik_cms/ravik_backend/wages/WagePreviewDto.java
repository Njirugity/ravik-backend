package net.ravik_cms.ravik_backend.wages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WagePreviewDto {
    private Long membershipId;
    private String userName;
    private String jobTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double grossPay;
    private Double baseWage;
    private long workedDays;
    private PaymentFrequency frequency;
    private UUID milestoneId;
    private String milestoneName;
}
