package net.ravik_cms.ravik_backend.milestones;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
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
    private int duration;
    private LocalDate earliestStart;
    private LocalDate earliestFinish;
    private LocalDate latestStart;
    private LocalDate latestFinish;
    private boolean critical;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private ProgressStatus status;
    private DateStatus dateStatus;
    private Double budget;
    private String phaseTitle;
}
