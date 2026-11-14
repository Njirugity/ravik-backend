package net.ravik_cms.ravik_backend.milestones.dtos;

import jakarta.validation.constraints.Min;
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
    @Min(value = 1, message = "Duration must be at least 1 day")
    private Integer duration;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;
}
