package net.ravik_cms.ravik_backend.projects.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.locations.dto.ProjectLocationDto;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPatchDto {
    private String title;
    private String plotNo;
    private Long wardId;
    private String street;
    private String type;
    private Double sizeSquareMeters;
    private ProgressStatus progress;
    private LocalDate plannedStart;
    private LocalDate contractFinish;
    // populated on responses only; ignored on requests
    private ProjectLocationDto location;
}
