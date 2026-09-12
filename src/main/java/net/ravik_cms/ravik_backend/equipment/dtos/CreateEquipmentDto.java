package net.ravik_cms.ravik_backend.equipment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEquipmentDto {
    private String title;
    private EquipmentCategory category;
    private Long capacity;
    private String metric;
}
