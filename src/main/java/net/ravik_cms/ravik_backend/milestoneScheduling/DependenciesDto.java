package net.ravik_cms.ravik_backend.milestoneScheduling;

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
public class DependenciesDto {
    private UUID milestoneId;
    private String milestoneTitle;
    private String milestoneDescription;
    private String phaseTitle;
    private int duration;
    private List<DependencyDto> dependencies = new ArrayList<>();
}
