package net.ravik_cms.ravik_backend.phase;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhasesMapper {
    PhasesInfoDto toPhaseInfoDto(Phases phases);
    Phases fromCreatePhaseDto(CreatePhaseDto createPhaseDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void UpdatePhase(UpdatePhaseDto dto, @MappingTarget Phases phases);
    List<PhasesInfoDto> toPhaseInfoList(List<Phases> phases);
}
