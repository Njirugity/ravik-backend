package net.ravik_cms.ravik_backend.milestones.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMilestoneDto {
    private String title;
    private String description;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
    private ProgressStatus status;
}
