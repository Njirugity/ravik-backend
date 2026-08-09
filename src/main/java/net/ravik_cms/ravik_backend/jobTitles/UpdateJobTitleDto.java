package net.ravik_cms.ravik_backend.jobTitles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateJobTitleDto {
    private String title;
    private Double baseWage;
    private PaymentFrequency frequency;
}
