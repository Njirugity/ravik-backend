package net.ravik_cms.ravik_backend.projects.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    @NotBlank(message = "Project title cannot be blank")
    private String title;
    private String plotNo;
    @NotNull(message = "Project's ward is required")
    private Long wardId;
    private String street;
    private String type;
    private Double sizeSquareMeters;
    private ProgressStatus progress;
    private LocalDate plannedStart;
    private LocalDate contractFinish;

    @AssertTrue(message = "Contract finish cannot be before the planned start")
    private boolean isContractFinishAfterStart() {
        return plannedStart == null || contractFinish == null || !contractFinish.isBefore(plannedStart);
    }
}
