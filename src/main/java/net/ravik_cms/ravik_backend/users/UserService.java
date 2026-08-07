package net.ravik_cms.ravik_backend.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.AttendanceService;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.client.ClientRepository;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.exception.UserAlreadyExistsException;
import net.ravik_cms.ravik_backend.dailyLog.DailyLog;
import net.ravik_cms.ravik_backend.dailyLog.DailyLogService;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final DailyLogService dailyLogService;
    private final AuthorizationService authorizationService;
    private final ClientRepository clientRepository;
    private final AttendanceService attendanceService;

    /**
     * Find a user in the membership through current project
     * @param project_id current project id
     * @param user_id user to search
     * @return user entity
     */
    public Users findUser(UUID project_id, UUID user_id){
        Users user = userRepository.findById(user_id).
                orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Projects project = projectsRepository.findById(project_id).
                orElseThrow(()->new ResourceNotFoundException("Project not found"));
        ProjectMembership membership = projectMembershipRepository.findByUserAndProject(user, project).
                orElseThrow(()-> new ResourceNotFoundException("Membership does not exit"));
        return user;
    }

    /**
     * Create a management level user
     * @param clientToAdd user dto for creating a client
     * @return client dto object
     */
    @Transactional
    public ClientDto addClient(CreateClientDto clientToAdd){
        Users newClient = userMapper.fromCreateClient(clientToAdd);
        newClient.setPassword(encoder.encode(newClient.getPassword()));
        Users newUser = userRepository.save(newClient);
        Client client = new Client();
        client.setId(newUser.getId());
        client.setEmail(newUser.getEmail());
        client.setUserName(newUser.getUserName());
        client.setPhoneNumber(newClient.getPhoneNumber());
        clientRepository.save(client);

        return userMapper.toClient(newUser);
    }

    /**
     * Create a supervision level user
     * @param supervisor user dto for creating a supervisor
     * @param id current project's id
     * @return supervisor dto object
     */
    @Transactional
    public SupervisorDto addSupervisor(CreateSupervisorsDto supervisor, UUID id){
        authorizationService.authorize( "CREATE_SUPERVISOR");
        Projects project = projectsRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));

        Roles roles = rolesRepository.findByNameAndProject(supervisor.getRoleKey(), project)
                .orElseThrow(()-> new ResourceNotFoundException("Role " + supervisor.getRoleKey()+ " not found"));
        DailyLog log = dailyLogService.createOrFetchDailyLog(project, LocalDate.now());

        Users newSupervisor = userMapper.fromCreateSupervisor(supervisor);
        Double wage = supervisor.getBaseDailyWage();
        newSupervisor.setPassword(encoder.encode(newSupervisor.getPassword()));
        newSupervisor.setDailyLog(log);
        userRepository.save(newSupervisor);

        membershipService.addToMembership(project, newSupervisor, roles, wage, RoleCategory.SUPERVISION,
                supervisor.getFrequency());
        return userMapper.toSupervisor(newSupervisor);
    }

    /**
     * Create a field crew level user
     * @param labourer user dto for creating a labourer
     * @param id current project's id
     * @return labourer dto object
     */
    @Transactional
    public LabourerDto addLabourer(CreateLabourerDto labourer, UUID id){
        authorizationService.authorize( "CREATE_LABOURER");
        Projects project = projectsRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Roles role = rolesRepository.findByNameAndProject(labourer.getRoleKey(), project).
                orElseThrow(()-> new ResourceNotFoundException("Role" + labourer.getRoleKey() + "not found"));
        DailyLog log = dailyLogService.createOrFetchDailyLog(project, LocalDate.now());

        Users newLabourer = userMapper.fromCreateLabourer(labourer);
        Double wage = labourer.getBaseDailyWage();
        newLabourer.setDailyLog(log);
        userRepository.save(newLabourer);

        membershipService.addToMembership(project, newLabourer, role, wage, RoleCategory.FIELD_CREW,
                labourer.getFrequency());
        return userMapper.toLabourer(newLabourer);
    }

    /**
     * Find all users by category
     * @param projectId current project's id
     * @param category role category
     * @return all membership with selected role
     */
    private List<ProjectMembership> getMembershipsByCategory(UUID projectId, RoleCategory category){
        Projects projects = projectsRepository.findById(projectId).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        return projectMembershipRepository.findAllByProjectAndRoleCategory(projects, category);
    }
    public SupervisorDto getClients(UUID project_id, UUID user_id){
        authorizationService.authorize("READ_CLIENTS");
        Users user = findUser(project_id, user_id);
        return userMapper.toSupervisor(user);
    }
    public SupervisorDto getSupervisor(UUID project_id, UUID user_id){
        authorizationService.authorize("READ_SUPERVISOR");
        Users user = findUser(project_id, user_id);
        return userMapper.toSupervisor(user);
    }
    public LabourerDto getLabourer(UUID project_id, UUID user_id){
        authorizationService.authorize("READ_LABOURER");
        Users user = findUser(project_id, user_id);
        return userMapper.toLabourer(user);
    }

    /**
     * Find all user with management category
     * @param projectId current project's id
     * @return all management users as supervisor dto
     */
    public List<SupervisorWithRoleDto> getAllManagement(UUID projectId){
        authorizationService.authorize( "READ_MANAGEMENT");
        List<ProjectMembership> memberships = getMembershipsByCategory(projectId, RoleCategory.MANAGEMENT);
        return memberships.stream()
                .map(userMapper::toSupervisorWithRole)
                .toList();
    }

    /**
     * Find all user with supervision category
     * @param projectId current project's id
     * @return all supervisor users as supervisor dto
     */
    public List<SupervisorWithRoleDto> getAllSupervisors(UUID projectId){
        authorizationService.authorize("READ_SUPERVISOR");
        List<ProjectMembership> memberships = getMembershipsByCategory(projectId, RoleCategory.SUPERVISION);
        return memberships.stream()
                .map(userMapper::toSupervisorWithRole)
                .toList();
    }

    /**
     * Find all user with field crew category
     * @param projectId current project's id
     * @return all field crew users as labourer dto
     */
    public List<LabourerWithRoleDto> getAllFieldCrew(UUID projectId){
        authorizationService.authorize("READ_LABOURER");
        List<ProjectMembership> memberships = getMembershipsByCategory(projectId, RoleCategory.FIELD_CREW);
        return memberships.stream()
                .map(userMapper::toLabourerWithRole)
                .toList();
    }

    /**
     * Find all users in a project
     * @param projectId current project's id
     * @return all users as staff dto
     */
    public List<StaffDto> getAllStaffByProject(UUID projectId){
        authorizationService.authorize("READ_STAFF");
        Projects project = projectsRepository.findById(projectId).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        List<ProjectMembership> membership= projectMembershipRepository.findAllByProject(project);
        return membership.stream()
                .map(ProjectMembership::getUser)
                .map(userMapper::toStaff)
                .toList();
    }

    /**
     * Find all users by project and role
     * @param projectId current project's id
     * @param role project roles
     * @return all users as staff dto
     */
    public List<StaffDto> getAllStaffByProjectAndRoles(UUID projectId, String role){
        authorizationService.authorize("READ_STAFF");
        Projects project = projectsRepository.findById(projectId).
                orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Roles roles = rolesRepository.findByNameAndProject(role, project).
                orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        List<ProjectMembership> memberships = projectMembershipRepository.findAllByProjectAndRole(project, roles);
        return memberships.stream()
                .map(ProjectMembership::getUser)
                .map(userMapper::toStaff)
                .toList();
    }

    /**
     * Edit a user
     * @param project_id current project's id
     * @param user_id user to edit
     * @param request update object
     * @return updated user
     */
    @Transactional
    public StaffDto updateUser(UUID project_id, UUID user_id, StaffDto request){
        authorizationService.authorize("UPDATE_STAFF");
        Users user = findUser(project_id, user_id);
        userMapper.updateUser(request, user);
        return userMapper.toStaff(user);
    }

    /**
     * Delete a user
     * @param project_id current project's id
     * @param user_id user to delete
     */
    public void deleteUser(UUID project_id, UUID user_id){
        authorizationService.authorize("DELETE_STAFF");
        Users user = findUser(project_id, user_id);
        userRepository.delete(user);
    }
}
