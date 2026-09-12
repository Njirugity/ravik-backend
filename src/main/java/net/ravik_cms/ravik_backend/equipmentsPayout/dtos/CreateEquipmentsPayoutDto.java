package net.ravik_cms.ravik_backend.equipmentsPayout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEquipmentsPayoutDto {
    private Long equipmentRequiredId;
    private long workedDuration;
    private long rentalCost;
    private long fuelCost;
    private long operatorCost;
    private LocalDate dateUsed;
    private String notes;
}
