package net.ravik_cms.ravik_backend.materials.mapper;

import net.ravik_cms.ravik_backend.common.mapper.EpochMillisMapper;
import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialRequired;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = EpochMillisMapper.class)
public interface MaterialRequiredMapper {
    @Mapping(source = "materialList.id", target = "materialListId")
    @Mapping(source = "materialList.name", target = "materialName")
    @Mapping(source = "materialList.metric", target = "materialMetric")
    @Mapping(source = "milestone.id", target = "milestoneId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy", target = "createdByUserId")
    @Mapping(target = "createdByUserName", ignore = true)
    @Mapping(target = "totalCost", expression = "java(materialRequired.getQuantityRequired() != null && materialRequired.getUnitPrice() != null ? materialRequired.getQuantityRequired() * materialRequired.getUnitPrice() : null)")
    MaterialRequiredDto toDto(MaterialRequired materialRequired);

    List<MaterialRequiredDto> toDtoList(List<MaterialRequired> materialRequiredList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateMaterialRequiredDto dto, @MappingTarget MaterialRequired materialRequired);
}
