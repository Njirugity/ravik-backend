package net.ravik_cms.ravik_backend.labourPayout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLabourPayoutDto {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Double totalAmount;
}
