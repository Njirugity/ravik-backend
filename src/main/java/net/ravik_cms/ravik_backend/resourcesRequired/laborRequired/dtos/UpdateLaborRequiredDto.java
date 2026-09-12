package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLaborRequiredDto {
    private Long jobTitleId;
    private long workersRequired;
}
