package net.ravik_cms.ravik_backend.projects.mapper;

import net.ravik_cms.ravik_backend.locations.mapper.LocationMapper;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectPatchDto;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = LocationMapper.class)
public interface ProjectMapper {
    @Mapping(source = "ward.id", target = "wardId")
    ProjectDto toProjectDto(Projects projects);
    List<ProjectDto> toProjectDtoList(List <Projects> projectsList);
    @Mapping(target = "ward", ignore = true)
    Projects toProjects(ProjectDto projectDto);
    @Mapping(source = "client.id", target = "client.id")
    @Mapping(source = "client.user.userName", target = "client.userName")
    @Mapping(source = "client.user.email", target = "client.email")
    @Mapping(source = "client.user.idNumber", target = "client.idNumber")
    @Mapping(source = "client.user.phoneNumber", target = "client.phoneNumber")
    @Mapping(source = "ward", target = "location")
    ProjectInfoDto toInfoDto(Projects projects);
    List<ProjectInfoDto> toInfoDtoList(List<Projects> projectsList);
    @Mapping(target = "ward", ignore = true)
    Projects fromInfoToProjects(ProjectInfoDto projectInfoDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "ward", ignore = true)
    void updateProjectFromDto(ProjectPatchDto dto, @MappingTarget Projects project);
    @Mapping(source = "ward.id", target = "wardId")
    @Mapping(source = "ward", target = "location")
    ProjectPatchDto toProjectPatchDto(Projects projects);
    @Mapping(target = "ward", ignore = true)
    Projects fromPatchToProjects(ProjectPatchDto projectPatchDto);
}
