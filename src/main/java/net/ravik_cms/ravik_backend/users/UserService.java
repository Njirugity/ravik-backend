package net.ravik_cms.ravik_backend.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.exception.UserAlreadyExistsException;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RolesRepository rolesRepository;
    private final ProjectsRepository projectsRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final ProjectMembershipService membershipService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();


    public Users findUser(UUID project_id, UUID user_id){
        Users user = userRepository.findById(user_id).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Projects project = projectsRepository.findById(project_id).
                orElseThrow(()->new ResourceNotFoundException("Project not found"));
        ProjectMembership membership = projectMembershipRepository.findByUserAndProject(user, project).
                orElseThrow(()-> new ResourceNotFoundException("Membership does not exit"));
        return user;
    }
    @Transactional
    public SupervisorDto addClient(CreateClientDto client){
        Users newClient = userMapper.fromCreateClient(client);
        newClient.setPassword(encoder.encode(newClient.getPassword()));
        return userMapper.toSupervisor(userRepository.save(newClient));
    }

    @Transactional
    public SupervisorDto addSupervisor(CreateSupervisorsDto supervisor, UUID id){
        Users newSupervisor = userMapper.fromCreateSupervisor(supervisor);
        Double wage = supervisor.getBaseDailyWage();
        newSupervisor.setPassword(encoder.encode(newSupervisor.getPassword()));
        userRepository.save(newSupervisor);

        Roles roles = rolesRepository.findByName(supervisor.getRoleKey())
                .orElseThrow(()-> new ResourceNotFoundException("Role " + supervisor.getRoleKey()+ " not found"));
        Projects project = projectsRepository.findById(id).orElseThrow();

        membershipService.addToMembership(project, newSupervisor, roles, wage);
        return userMapper.toSupervisor(newSupervisor);
    }
    @Transactional
    public LabourerDto addLabourer(CreateLabourerDto labourer, UUID id){
        Users newLabourer = userMapper.fromCreateLabourer(labourer);
        Double wage = labourer.getBaseDailyWage();
        userRepository.save(newLabourer);
        Projects project = projectsRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Roles role = rolesRepository.findByName(labourer.getRoleKey()).
                orElseThrow(()-> new ResourceNotFoundException("Role" + labourer.getRoleKey() + "not found"));

        membershipService.addToMembership(project, newLabourer, role, wage);
        return userMapper.toLabourer(newLabourer);
    }

    public List<SupervisorDto> getAllClients(){
        return userMapper.toSupervisorList(userRepository.findAll());
    }
    public SupervisorDto getSupervisor(UUID project_id, UUID user_id){
        Users users = findUser(project_id, user_id);
        return userMapper.toSupervisor(users);
    }
    public LabourerDto getLabourer(UUID project_id, UUID user_id){
        Users users = findUser(project_id, user_id);
        return userMapper.toLabourer(users);
    }

    public List<StaffDto> getAllStaffByProject(UUID id){
        Projects project = projectsRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        List<ProjectMembership> membership= projectMembershipRepository.findAllByProject(project);
        return membership.stream()
                .map(ProjectMembership::getUser)
                .map(userMapper::toStaff)
                .toList();
    }
    public List<StaffDto> getAllStaffByProjectAndRoles(UUID id, String role){
        Projects project = projectsRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Roles roles = rolesRepository.findByName(role).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        List<ProjectMembership> memberships = projectMembershipRepository.findAllByProjectAndRole(project, roles);
        return memberships.stream()
                .map(ProjectMembership::getUser)
                .map(userMapper::toStaff)
                .toList();
    }
    @Transactional
    public StaffDto updateUser(UUID project_id, UUID user_id, StaffDto request){
        Users user = findUser(project_id, user_id);
        userMapper.updateUser(request, user);
        return userMapper.toStaff(user);
    }
    public void deleteUser(UUID project_id, UUID user_id){
        Users user = findUser(project_id, user_id);
        userRepository.delete(user);
    }
}
