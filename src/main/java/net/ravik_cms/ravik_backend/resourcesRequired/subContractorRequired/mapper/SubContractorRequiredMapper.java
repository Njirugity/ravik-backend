package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.mapper;

import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.CreateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.UpdateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SubContractorRequiredMapper {
    SubContractorRequired toEntity(CreateSubContractorRequiredDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubContractorRequired(UpdateSubContractorRequiredDto dto, @MappingTarget SubContractorRequired entity);
}
