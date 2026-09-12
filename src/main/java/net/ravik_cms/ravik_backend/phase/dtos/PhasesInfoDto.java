package net.ravik_cms.ravik_backend.phase.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhasesInfoDto {
    private UUID id;
    private String title;
    private String description;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private Double budget;
    private ProgressStatus status;
    private Long totalMilestones;
    private Long totalMilestonesCompleted;
}
