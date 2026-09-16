package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVisualizationResponseDto {
    private AONDiagramDto aonDiagram;
    private GanttChartDto ganttChart;
    private CriticalPathDetailsDto criticalPathDetails;
    private ScheduleSummary scheduleSummary;
    private List<MilestoneStatusDTO> milestoneStatuses;
}
