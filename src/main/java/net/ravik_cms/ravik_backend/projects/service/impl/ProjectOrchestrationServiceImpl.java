package net.ravik_cms.ravik_backend.projects.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.client.ClientRepository;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipService;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectDto;
import net.ravik_cms.ravik_backend.projects.dtos.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.mapper.ProjectMapper;
import net.ravik_cms.ravik_backend.projects.service.ProjectOrchestrationService;
import net.ravik_cms.ravik_backend.projects.service.ProjectService;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectOrchestrationServiceImpl implements ProjectOrchestrationService {
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final ProjectMembershipService membershipService;
    private final RolesRepository rolesRepository;
    private final ProjectMapper projectMapper;
    private final ClientRepository clientRepository;

    @Override
    @Transactional
    public ProjectInfoDto createProject(ProjectDto request, String userName){
        Users user = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Client client = clientRepository.findById(user.getId()).
                orElseThrow(()-> new ResourceNotFoundException("Client profile not found"));

        Projects project = projectService.createProject(request, client);

        Roles role = rolesRepository.findByNameAndProject("OWNER", project).
                orElseThrow(()->new ResourceNotFoundException("Role not found"));
        membershipService.createOwnerMembership(user, project, role);
        return projectMapper.toInfoDto(project);
    }

}
