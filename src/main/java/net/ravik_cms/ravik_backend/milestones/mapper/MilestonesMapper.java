package net.ravik_cms.ravik_backend.milestones.mapper;

import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.milestones.dtos.CreateMilestoneDto;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneInfoDto;
import net.ravik_cms.ravik_backend.milestones.dtos.UpdateMilestoneDto;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.service.DateEvaluator;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class MilestonesMapper {

@Autowired
protected DateEvaluator dateEvaluator; // Injecting your service layer logic

@Mapping(source = "phase.title", target = "phaseTitle")
@Mapping(target = "dateStatus", expression = "java(callServiceStatus(milestones))")
@Mapping(source = "forecastES", target = "forecastStart")
@Mapping(source = "forecastEF", target = "forecastFinish")
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
