package net.ravik_cms.ravik_backend.milestones.dtos;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateMilestoneDto {
    private String title;
    private String description;
    @Min(value = 1, message = "Duration must be at least 1 day")
    private int duration;
}
