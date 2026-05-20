package net.ravik_cms.ravik_backend.phase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhasesInfographicsDto {
    private Long totalPhases;
    private String activePhase;
    private LocalDate plannedEndDate;
    private Double totalSpent;
}
