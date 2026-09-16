package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDependenciesDto {
    private UUID projectId;
    private List<DependenciesDto> dependencies = new ArrayList<>();
}
