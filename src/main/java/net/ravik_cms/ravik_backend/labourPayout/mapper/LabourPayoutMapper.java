package net.ravik_cms.ravik_backend.labourPayout.mapper;

import net.ravik_cms.ravik_backend.labourPayout.dtos.CreateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.dtos.UpdateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.entity.LabourPayout;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LabourPayoutMapper {
    LabourPayout toEntity(CreateLabourPayoutDto dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLabourPayout(UpdateLabourPayoutDto dto, @MappingTarget LabourPayout entity);
}
