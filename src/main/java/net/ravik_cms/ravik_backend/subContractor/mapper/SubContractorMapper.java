package net.ravik_cms.ravik_backend.subContractor.mapper;

import net.ravik_cms.ravik_backend.subContractor.dtos.CreateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.dtos.UpdateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SubContractorMapper {
    SubContractor toEntity(CreateSubContractorDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubContractor(UpdateSubContractorDto dto, @MappingTarget SubContractor entity);
}
