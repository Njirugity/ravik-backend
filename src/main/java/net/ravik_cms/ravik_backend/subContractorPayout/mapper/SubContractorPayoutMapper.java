package net.ravik_cms.ravik_backend.subContractorPayout.mapper;

import net.ravik_cms.ravik_backend.subContractorPayout.dtos.CreateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.UpdateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.entity.SubContractorPayout;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface SubContractorPayoutMapper {
    @Mapping(target = "actualJobCost", source = "actualJobAmount")
    SubContractorPayout toEntity(CreateSubContractorPayoutDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSubContractorPayout(UpdateSubContractorPayoutDto dto, @MappingTarget SubContractorPayout entity);
}
