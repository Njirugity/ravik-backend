package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEquipmentRequiredDto {
    private Long equipmentId;
    private long plannedDuration;
    private long plannedFuelCost;
    private String notes;
    private LocalDate dateRequired;
}
