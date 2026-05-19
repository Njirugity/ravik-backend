package net.ravik_cms.ravik_backend.projects;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.client.ClientRepository;
import net.ravik_cms.ravik_backend.common.dataInitializer.RolesSeeder;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipService;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectOrchestrationService {
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final ProjectMembershipService membershipService;
    private final RolesRepository rolesRepository;
    private final ProjectMapper projectMapper;
    private final ClientRepository clientRepository;

    @Transactional
    public ProjectInfoDto createProject(ProjectDto request, String userName){
        Users user = userRepository.findByUserName(userName).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Client client = clientRepository.findById(user.getId()).
                orElseThrow(()-> new ResourceNotFoundException("Client profile not found"));

        Projects project = projectService.createProject(request, userName);

        client.getProjects().add(project);
        Roles role = rolesRepository.findByNameAndProject("OWNER", project).
                orElseThrow(()->new ResourceNotFoundException("Role not found"));
        membershipService.createOwnerMembership(user, project, role);
        return projectMapper.toInfoDto(project);
    }

}
