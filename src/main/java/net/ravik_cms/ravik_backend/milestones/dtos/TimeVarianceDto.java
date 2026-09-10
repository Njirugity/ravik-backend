package net.ravik_cms.ravik_backend.milestones.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeVarianceDto {
    private UUID id;
    private String title;
    private LocalDate plannedEndDate;
    private Long daysOverdue;
    private String severity;

}
