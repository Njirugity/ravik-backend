package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GanttTaskDto {
    private UUID id;
    private String title;
    private int duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isCritical;
    private Long totalFloat;
    private double progress;
    private List<GanttDependencyDto> dependencies;
    private String color;
    private int rowIndex;
    private boolean isMilestone;
    private UUID phaseId;
    private String phaseTitle;
    private LocalDate forecastStart;
    private LocalDate forecastFinish;
    private Long forecastFloat;
    private boolean forecastCritical;
}
