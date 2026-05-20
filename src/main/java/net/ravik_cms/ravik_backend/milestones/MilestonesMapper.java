package net.ravik_cms.ravik_backend.milestones;

import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MilestonesMapper {
    @Mapping(source = "phase.title", target = "phaseTitle")
    MilestoneInfoDto toInfoDto(Milestones milestones);
    Milestones toEntityFromInfoDto(MilestoneInfoDto dto);
    Milestones toEntity(CreateMilestoneDto dto);
    List<MilestoneInfoDto> toInfoDtoList(List<Milestones> milestones);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMilestone(UpdateMilestoneDto dto, @MappingTarget Milestones milestones);
}
