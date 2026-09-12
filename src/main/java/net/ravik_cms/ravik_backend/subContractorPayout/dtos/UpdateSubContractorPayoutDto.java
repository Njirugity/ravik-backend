package net.ravik_cms.ravik_backend.subContractorPayout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubContractorPayoutDto {
    private Long subContractorRequiredId;
    private Double actualJobCost;
    private Double paidAmount;
    private LocalDate jobDate;
}
