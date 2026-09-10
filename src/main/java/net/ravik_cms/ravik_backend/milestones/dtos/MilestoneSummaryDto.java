package net.ravik_cms.ravik_backend.milestones.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MilestoneSummaryDto {
    private long totalMilestones;
    private long overdue;
    private long inProgress;
    private long completed;
    private long pending;
}
