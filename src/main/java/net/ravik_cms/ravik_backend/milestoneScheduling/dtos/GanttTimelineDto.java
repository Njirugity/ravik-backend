package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanttTimelineDto {
    private LocalDate minDate;
    private LocalDate maxDate;
    private List<LocalDate> workingDays;
    private List<LocalDate> holidays;
    private int totalDays;
}
