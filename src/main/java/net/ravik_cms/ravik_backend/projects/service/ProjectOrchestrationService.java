package net.ravik_cms.ravik_backend.projects.service;

import jakarta.transaction.Transactional;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;

public interface ProjectOrchestrationService {
    ProjectInfoDto createProject(ProjectDto request, String userName);
}
