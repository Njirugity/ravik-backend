package net.ravik_cms.ravik_backend.projects;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.dataInitializer.RolesSeeder;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectsRepository projectsRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final RolesSeeder rolesSeeder;

    public Projects findProject(UUID id, String userName){
        Users users = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User "+ userName + "not found"));
        Projects project = projectsRepository.findById(id).orElseThrow();
        ProjectMembership member = projectMembershipRepository.findByUserAndProject(users, project).
                orElseThrow(()-> new AccessDeniedException("Not A project Member"));
        return project;
    }

    @Transactional
    public Projects createProject(ProjectDto projectDto, String userName){
        Users client = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Projects newProject = projectMapper.toProjects(projectDto);
        newProject.setClient(client);
        projectsRepository.save(newProject);
        rolesSeeder.seedDefaultRoles(newProject);
        return newProject;
    }
    @Transactional
    public ProjectInfoDto getProject(UUID id, String userName){
        Projects project = findProject(id, userName);
        return projectMapper.toInfoDto(project);
    }

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
    @Transactional
    public ProjectPatchDto patchProject(UUID id,String userName, ProjectPatchDto request){
        Projects project = findProject(id, userName);
        projectMapper.updateProjectFromDto(request, project);
        return projectMapper.toProjectPatchDto(projectsRepository.save(project));
    }
    @Transactional
    public void deleteProject(UUID id, String userName){
        Projects project = findProject(id, userName);
        projectsRepository.delete(project);
    }
}
