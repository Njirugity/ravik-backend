package net.ravik_cms.ravik_backend.projects;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toProjectDto(Projects projects);
    List<ProjectDto> toProjectDtoList(List <Projects> projectsList);
    Projects toProjects(ProjectDto projectDto);
    @Mapping(source = "client.id", target = "client.id")
    @Mapping(source = "client.user.userName", target = "client.userName")
    @Mapping(source = "client.user.email", target = "client.email")
    @Mapping(source = "client.user.idNumber", target = "client.idNumber")
    @Mapping(source = "client.user.phoneNumber", target = "client.phoneNumber")
    ProjectInfoDto toInfoDto(Projects projects);
    List<ProjectInfoDto> toInfoDtoList(List<Projects> projectsList);
    Projects fromInfoToProjects(ProjectInfoDto projectInfoDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDto(ProjectPatchDto dto, @MappingTarget Projects project);
    ProjectPatchDto toProjectPatchDto(Projects projects);
    Projects fromPatchToProjects(ProjectPatchDto projectPatchDto);
}
