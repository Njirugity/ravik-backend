package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.mapper;

import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.CreateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.UpdateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.entity.LaborRequired;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LaborRequiredMapper {
    LaborRequired toEntity(CreateLaborRequiredDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLaborRequired(UpdateLaborRequiredDto dto, @MappingTarget LaborRequired entity);
}
