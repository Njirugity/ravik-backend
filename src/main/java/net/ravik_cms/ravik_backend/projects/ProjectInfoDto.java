package net.ravik_cms.ravik_backend.projects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.ravik_cms.ravik_backend.users.SupervisorDto;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDto {
    private UUID id;
    private String title;
    private String location;
    private String plotNo;
    private SupervisorDto client;
}
