package net.ravik_cms.ravik_backend.projects.service;

import jakarta.transaction.Transactional;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectPatchDto;
import net.ravik_cms.ravik_backend.projects.entity.Projects;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    Projects createProject(ProjectDto projectDto, Client client);
    ProjectInfoDto getProject(UUID id, String userName);
    List<ProjectInfoDto> getAllProjects(String userName);
    ProjectPatchDto patchProject(UUID id, String userName, ProjectPatchDto request);
    void deleteProject(UUID id, String userName);
}
