package net.ravik_cms.ravik_backend.milestoneScheduling.dtos;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneStatusDTO {
    private UUID id;
    private String title;
    @Enumerated(EnumType.STRING)
    private ProgressStatus status;
    private LocalDate actualStart;
    private LocalDate actualFinish;
    private Double completionPercentage;
    private boolean isDelayed;
    private long delayDays;
    private String color;
}
