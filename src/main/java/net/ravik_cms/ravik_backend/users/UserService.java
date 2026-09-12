package net.ravik_cms.ravik_backend.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.AttendanceService;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.authorization.UserProjectContext;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.client.ClientRepository;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.exception.FieldRequiredException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.exception.UserAlreadyExistsException;
import net.ravik_cms.ravik_backend.dailyLog.DailyLog;
import net.ravik_cms.ravik_backend.dailyLog.DailyLogService;
import net.ravik_cms.ravik_backend.jobTitles.JobTitles;
import net.ravik_cms.ravik_backend.jobTitles.JobTitlesRepository;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipRepository;
import net.ravik_cms.ravik_backend.memberships.ProjectMembershipService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.roles.RolesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final JobTitlesRepository jobTitlesRepository;

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
        if (userRepository.findByUserName(clientToAdd.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        Users newUser = userMapper.fromCreateClient(clientToAdd);
        newUser.setPassword(encoder.encode(newUser.getPassword()));
        newUser = userRepository.save(newUser);

        Client client = new Client();
        client.setUser(newUser);
        clientRepository.save(client);

        return userMapper.toClient(newUser);
    }
    @Transactional
    public void addUser(UUID projectId, CreateUserDto request){
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Users user = userMapper.fromCreateUsers(request);
        DailyLog log = dailyLogService.createOrFetchDailyLog(project, LocalDate.now());
        Roles role = null;
        JobTitles jobTitle = null;
        if(request.isHasSystemAccess()){
            if(request.getRoleId() == null){
                throw new ResourceNotFoundException("Role is required when system access is enabled");
            }
            role = rolesRepository.findById(request.getRoleId())
                    .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        }
        if(request.getJobTitleId() != null){
            jobTitle = jobTitlesRepository.findById(request.getJobTitleId())
                    .orElseThrow(()-> new ResourceNotFoundException("Job title not found"));
        }
        if(request.isUpdateJobTitle()){
            if (jobTitle == null) {
                throw new FieldRequiredException("Job title is required");
            }
            if (request.getBaseWage() == null) {
                throw new FieldRequiredException("Base wage is required");
            }
            if (request.getFrequency() == null) {
                throw new FieldRequiredException("Wage frequency is required");
            }
            jobTitle.setBaseWage(request.getBaseWage());
            jobTitle.setFrequency(request.getFrequency());

        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.setDailyLog(log);
        userRepository.save(user);
        membershipService.addToMembership(project, user, role, request.getBaseWage(),
                jobTitle,request.getFrequency(), request.isGenerateAttendance());

    }

    /**
     * Fetch the full detail (KYC + membership) for a single user in a project
     * @param project_id current project's id
     * @param user_id user to fetch
     * @return user detail dto
     */
    public UserDetailDto getUserDetail(UUID project_id, UUID user_id){
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        ProjectMembership membership = projectMembershipRepository.findByUserAndProject(user, project)
                .orElseThrow(()-> new ResourceNotFoundException("Membership does not exist"));
        return toUserDetailDto(user, membership);
    }

    private UserDetailDto toUserDetailDto(Users user, ProjectMembership membership){
        UserDetailDto dto = new UserDetailDto();
        dto.setId(user.getId());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setIdNumber(user.getIdNumber());
        dto.setMemberId(membership.getId());
        if (membership.getRole() != null) {
            dto.setRoleId(membership.getRole().getId());
            dto.setRoleName(membership.getRole().getName());
        }
        if (membership.getJobTitle() != null) {
            dto.setJobTitleId(membership.getJobTitle().getId());
            dto.setJobTitleName(membership.getJobTitle().getTitle());
        }
        dto.setBaseWage(membership.getBaseWage());
        dto.setFrequency(membership.getFrequency());
        dto.setGenerateAttendance(membership.isGenerateAttendance());
        dto.setStatus(membership.getStatus());
        return dto;
    }

    /**
     * Edit a user's KYC details and project membership (role, job title, wage, status)
     * @param project_id current project's id
     * @param user_id user to edit
     * @param request update object
     * @return updated user detail
     */
    @Transactional
    public UserDetailDto updateUser(UUID project_id, UUID user_id, UpdateUserDto request){
        authorizationService.authorize("UPDATE_USER");
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        Projects project = projectsRepository.findById(project_id)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        ProjectMembership membership = projectMembershipRepository.findByUserAndProject(user, project)
                .orElseThrow(()-> new ResourceNotFoundException("Membership does not exist"));

        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setIdNumber(request.getIdNumber());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(encoder.encode(request.getPassword()));
        }

        Roles role = null;
        JobTitles jobTitle = null;
        if(request.isHasSystemAccess()){
            if(request.getRoleId() == null){
                throw new ResourceNotFoundException("Role is required when system access is enabled");
            }
            role = rolesRepository.findById(request.getRoleId())
                    .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        }
        if(request.getJobTitleId() != null){
            jobTitle = jobTitlesRepository.findById(request.getJobTitleId())
                    .orElseThrow(()-> new ResourceNotFoundException("Job title not found"));
        }
        if(request.isUpdateJobTitle()){
            if (jobTitle == null) {
                throw new ResourceNotFoundException("Job title is required");
            }
            if (request.getBaseWage() == null) {
                throw new ResourceNotFoundException("Base wage is required");
            }
            if (request.getFrequency() == null) {
                throw new ResourceNotFoundException("Wage frequency is required");
            }
            jobTitle.setBaseWage(request.getBaseWage());
            jobTitle.setFrequency(request.getFrequency());
        }

        membership.setRole(role);
        membership.setJobTitle(jobTitle);
        membership.setBaseWage(request.getBaseWage());
        membership.setFrequency(request.getFrequency());
        membership.setGenerateAttendance(request.isGenerateAttendance());
        if (request.getStatus() != null) {
            membership.setStatus(request.getStatus());
        }

        return toUserDetailDto(user, membership);
    }

    /**
     * Delete a user
     * @param project_id current project's id
     * @param user_id user to delete
     */
    public void deleteUser(UUID project_id, UUID user_id){
        authorizationService.authorize("DELETE_USER");
        Users user = findUser(project_id, user_id);
        userRepository.delete(user);
    }

    /**
     * List users belonging to a project, optionally filtered by role, job title, or search term
     * @param project_id current project's id
     * @param role role name to filter by
     * @param jobTitle job title to filter by
     * @param search search term matched against username
     * @param pageable pagination information
     * @return page of user details
     */
    public Page<UserDetailsProjection> getAllUsers(UUID project_id, String role, String jobTitle,
                                                    String search, Pageable pageable){
        return userRepository.findAllUsers(project_id, role, jobTitle, search, pageable);
    }
}
