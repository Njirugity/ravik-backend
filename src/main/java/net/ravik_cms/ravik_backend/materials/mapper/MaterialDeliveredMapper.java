package net.ravik_cms.ravik_backend.materials.mapper;

import net.ravik_cms.ravik_backend.common.mapper.EpochMillisMapper;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialDelivered;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = EpochMillisMapper.class)
public interface MaterialDeliveredMapper {
    @Mapping(source = "materialList.id", target = "materialListId")
    @Mapping(source = "materialList.name", target = "materialName")
    @Mapping(source = "materialList.metric", target = "materialMetric")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy", target = "createdByUserId")
    @Mapping(target = "createdByUserName", ignore = true)
    @Mapping(target = "totalCost", expression = "java(materialDelivered.getQuantityDelivered() != null && materialDelivered.getUnitPrice() != null ? materialDelivered.getQuantityDelivered() * materialDelivered.getUnitPrice() : null)")
    MaterialDeliveredDto toDto(MaterialDelivered materialDelivered);

    List<MaterialDeliveredDto> toDtoList(List<MaterialDelivered> materialDeliveredList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateMaterialDeliveredDto dto, @MappingTarget MaterialDelivered materialDelivered);
}
