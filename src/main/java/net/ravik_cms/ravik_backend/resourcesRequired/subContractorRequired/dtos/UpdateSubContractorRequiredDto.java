package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSubContractorRequiredDto {
    private Long subContractorId;
    private LocalDate dateRequired;
    private Double jobAmount;
    private String jobTitle;
    private String description;
}
