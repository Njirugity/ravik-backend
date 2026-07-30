package net.ravik_cms.ravik_backend.milestones;

import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MilestonesMapper {

@Autowired
protected DateEvaluator dateEvaluator; // Injecting your service layer logic

@Mapping(source = "phase.title", target = "phaseTitle")
@Mapping(target = "dateStatus", expression = "java(callServiceStatus(milestones))")
public abstract MilestoneInfoDto toInfoDto(Milestones milestones);
public abstract List<MilestoneInfoDto> toInfoDtoList(List<Milestones> milestones);
public abstract Milestones toEntityFromInfoDto(MilestoneInfoDto dto);
public abstract Milestones toEntity(CreateMilestoneDto dto);
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract void updateMilestone(UpdateMilestoneDto dto, @MappingTarget Milestones milestones);

protected DateStatus callServiceStatus(Milestones milestones) {
    if (milestones == null) {
        return null;
    }
    return dateEvaluator.calculateDateStatus(milestones);
}
}
