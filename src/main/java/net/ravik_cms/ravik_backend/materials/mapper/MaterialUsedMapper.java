package net.ravik_cms.ravik_backend.materials.mapper;

import net.ravik_cms.ravik_backend.common.mapper.EpochMillisMapper;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialUsed;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = EpochMillisMapper.class)
public interface MaterialUsedMapper {
    @Mapping(source = "materialDelivered.id", target = "materialDeliveredId")
    @Mapping(source = "materialDelivered.materialList.id", target = "materialListId")
    @Mapping(source = "materialDelivered.materialList.name", target = "materialName")
    @Mapping(source = "materialDelivered.materialList.metric", target = "materialMetric")
    @Mapping(source = "milestone.id", target = "milestoneId")
    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "createdBy", target = "createdByUserId")
    @Mapping(target = "createdByUserName", ignore = true)
    MaterialUsedDto toDto(MaterialUsed materialUsed);

    List<MaterialUsedDto> toDtoList(List<MaterialUsed> materialUsedList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateMaterialUsedDto dto, @MappingTarget MaterialUsed materialUsed);
}
