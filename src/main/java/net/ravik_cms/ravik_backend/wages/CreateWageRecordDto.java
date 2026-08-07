package net.ravik_cms.ravik_backend.wages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWageRecordDto {
    private Long memberId;
    private Double baseWage;
    private Double grossPay;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long workedDays;
}
