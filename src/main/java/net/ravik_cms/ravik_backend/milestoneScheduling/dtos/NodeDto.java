package net.ravik_cms.ravik_backend.milestoneScheduling;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeDto {
    private UUID id;
    private String title;
    private int duration;
    private LocalDate earliestStart;
    private LocalDate earliestFinish;
    private LocalDate latestStart;
    private LocalDate latestFinish;
    private Long totalFloat;
    private boolean isCritical;
    private boolean isMilestone;
    @Enumerated(EnumType.STRING)
    private ProgressStatus status;
    private int x;
    private int y;
    private String color;
}
