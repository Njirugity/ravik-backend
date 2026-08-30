package net.ravik_cms.ravik_backend.equipment.mapper;

import net.ravik_cms.ravik_backend.equipment.dtos.UpdateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.dtos.CreateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipments toEntity(CreateEquipmentDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEquipment(UpdateEquipmentDto dto, @MappingTarget Equipments entity);
}
