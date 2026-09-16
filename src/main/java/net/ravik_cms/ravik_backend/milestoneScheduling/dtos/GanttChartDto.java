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
public class GanttChartDto {
    private List<GanttTaskDto> tasks;
    private GanttTimelineDto timeline;
    private UUID projectId;
    private String projectTitle;
    private LocalDate projectStart;
    private LocalDate projectEnd;
    private int totalWorkingDays;
}
