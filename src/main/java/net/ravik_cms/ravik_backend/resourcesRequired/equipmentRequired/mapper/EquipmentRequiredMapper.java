package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.mapper;

import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.CreateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.UpdateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EquipmentRequiredMapper {
    EquipmentRequired toEntity(CreateEquipmentRequiredDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEquipmentRequired(UpdateEquipmentRequiredDto dto, @MappingTarget EquipmentRequired entity);
}
