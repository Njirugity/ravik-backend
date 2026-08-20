package net.ravik_cms.ravik_backend.projects;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotBlank(message = "Project title cannot be blank")
    private String title;
    @NotBlank(message = "Project's location cannot be blank")
    private String location;
    private String plotNo;
    private String address;
    private String type;
    private Double sizeSquareMeters;
    private ProgressStatus progress;
    private LocalDate plannedStart;
}
