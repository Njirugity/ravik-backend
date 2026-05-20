package net.ravik_cms.ravik_backend.projects;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String NCAregNumber;
    private String NEMAregNumber;
    private String CountyRegNumber;
}
