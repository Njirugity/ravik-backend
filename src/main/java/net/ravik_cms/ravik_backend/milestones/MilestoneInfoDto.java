package net.ravik_cms.ravik_backend.milestones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.phase.Phases;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneInfoDto {
    private String id;
    private String title;
    private String description;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private Double budget;
    private String phaseTitle;
}
