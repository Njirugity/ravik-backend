package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import net.ravik_cms.ravik_backend.milestones.entity.Milestones;

import java.util.List;
import java.util.UUID;

public record MilestoneAndDependentsDto(
        UUID milestoneId,
        List<Milestones> predecessors
){}
