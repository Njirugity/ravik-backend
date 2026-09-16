package net.ravik_cms.ravik_backend.milestoneScheduling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriticalPathDetailsDto {
    private List<UUID> criticalPathIds;
    private int totalCriticalTasks;
    private long totalDurationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private double criticalPathPercentage;
}
