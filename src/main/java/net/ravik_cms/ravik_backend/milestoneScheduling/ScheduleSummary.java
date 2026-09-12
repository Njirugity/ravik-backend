package net.ravik_cms.ravik_backend.milestoneScheduling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleSummary {
    private UUID projectId;
    private String projectName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long criticalPathDuration;
    private int criticalMilestoneCount;
    private int totalMilestoneCount;
    private List<Milestones> criticalPath;
}
