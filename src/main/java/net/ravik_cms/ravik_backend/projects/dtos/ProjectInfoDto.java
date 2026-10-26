package net.ravik_cms.ravik_backend.projects.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.locations.dto.ProjectLocationDto;
import net.ravik_cms.ravik_backend.users.ClientDto;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDto {
    private UUID id;
    private String title;
    private String plotNo;
    private String street;
    private ProjectLocationDto location;
    private String type;
    private Double sizeSquareMeters;
    private ProgressStatus progress;
    private ClientDto client;
    private LocalDate plannedStart;
    private LocalDate contractFinish;
}
