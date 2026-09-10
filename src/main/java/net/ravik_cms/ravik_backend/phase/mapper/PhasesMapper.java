package net.ravik_cms.ravik_backend.phase.mapper;

import net.ravik_cms.ravik_backend.phase.dtos.CreatePhaseDto;
import net.ravik_cms.ravik_backend.phase.dtos.PhasesInfoDto;
import net.ravik_cms.ravik_backend.phase.dtos.UpdatePhaseDto;
import net.ravik_cms.ravik_backend.phase.entity.Phases;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PhasesMapper {
    PhasesInfoDto toPhaseInfoDto(Phases phases);
    Phases fromCreatePhaseDto(CreatePhaseDto createPhaseDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdatePhase(UpdatePhaseDto dto, @MappingTarget Phases phases);
}
