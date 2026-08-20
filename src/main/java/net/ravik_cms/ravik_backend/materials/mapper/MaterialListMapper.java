package net.ravik_cms.ravik_backend.materials.mapper;

import net.ravik_cms.ravik_backend.common.mapper.EpochMillisMapper;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = EpochMillisMapper.class)
public interface MaterialListMapper {
    @Mapping(source = "project.id", target = "projectId")
    MaterialListDto toDto(MaterialList materialList);

    List<MaterialListDto> toDtoList(List<MaterialList> materialLists);

    MaterialList fromCreateDto(CreateMaterialListDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateMaterialListDto dto, @MappingTarget MaterialList materialList);
}
