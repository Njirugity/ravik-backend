package net.ravik_cms.ravik_backend.equipmentsPayout.mapper;

import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.CreateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.UpdateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.entity.EquipmentsPayout;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EquipmentsPayoutMapper {
    EquipmentsPayout toEntity(CreateEquipmentsPayoutDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEquipmentsPayout(UpdateEquipmentsPayoutDto dto, @MappingTarget EquipmentsPayout entity);
}
