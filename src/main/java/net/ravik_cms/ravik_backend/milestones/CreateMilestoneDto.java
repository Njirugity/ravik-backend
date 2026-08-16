package net.ravik_cms.ravik_backend.milestones;

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
    private int duration;
}
