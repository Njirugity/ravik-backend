package net.ravik_cms.ravik_backend.projects;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toProjectDto(Projects projects);
    List<ProjectDto> toProjectDtoList(List <Projects> projectsList);
    Projects toProjects(ProjectDto projectDto);
    ProjectInfoDto toInfoDto(Projects projects);
    List<ProjectInfoDto> toInfoDtoList(List<Projects> projectsList);
    Projects fromInfoToProjects(ProjectInfoDto projectInfoDto);
    ProjectPatchDto toProjectPatchDto(Projects projects);
    Projects fromPatchToProjects(ProjectPatchDto projectPatchDto);
}
