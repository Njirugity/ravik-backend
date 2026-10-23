package net.ravik_cms.ravik_backend.projects.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.common.dataInitializer.RolesSeeder;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.locations.service.LocationService;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectPatchDto;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.mapper.ProjectMapper;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import net.ravik_cms.ravik_backend.projects.service.ProjectService;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectsRepository projectsRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final RolesSeeder rolesSeeder;
    private final AuthorizationService authorizationService;
    private final LocationService locationService;

    @Override
    @Transactional
    public Projects createProject(ProjectDto projectDto, Client client){
        Projects newProject = projectMapper.toProjects(projectDto);
        newProject.setWard(locationService.getWard(projectDto.getWardId()));
        newProject.setClient(client);
        projectsRepository.save(newProject);
        rolesSeeder.seedDefaultRoles(newProject);
        return newProject;
    }
    @Override
    @Transactional
    public ProjectInfoDto getProject(UUID id, String userName){
        Projects project = findProject(id, userName);
        return projectMapper.toInfoDto(project);
    }

    @Override
    public List<ProjectInfoDto> getAllProjects(String userName){

        Users user = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        List<ProjectMembership> memberships =
                projectMembershipRepository.findAllByUser(user);

        return memberships.stream()
                .map(ProjectMembership::getProject)
                .map(projectMapper::toInfoDto)
                .toList();
    }
    @Override
    @Transactional
    public ProjectPatchDto patchProject(UUID id, String userName, ProjectPatchDto request){
        authorizationService.authorize("UPDATE_PROJECT");
        Projects project = findProject(id, userName);
        projectMapper.updateProjectFromDto(request, project);
        if(request.getWardId() != null){
            project.setWard(locationService.getWard(request.getWardId()));
        }
        return projectMapper.toProjectPatchDto(projectsRepository.save(project));
    }
    @Override
    @Transactional
    public void deleteProject(UUID id, String userName){
        authorizationService.authorize("DELETE_PROJECT");
        Projects project = findProject(id, userName);
        projectsRepository.delete(project);
    }

    private Projects findProject(UUID id, String userName){
        Users users = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User "+ userName + "not found"));
        Projects project = projectsRepository.findById(id).orElseThrow();
        ProjectMembership member = projectMembershipRepository.findByUserAndProject(users, project).
                orElseThrow(()-> new AccessDeniedException("Not A project Member"));
        return project;
    }
}
