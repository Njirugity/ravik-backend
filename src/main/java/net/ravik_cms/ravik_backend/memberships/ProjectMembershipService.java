package net.ravik_cms.ravik_backend.memberships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectMembershipService {
    private final ProjectMembershipRepository membershipRepository;

    public void createOwnerMembership(Users user, Projects projects, Roles roles){
        ProjectMembership membership = new ProjectMembership();
        membership.setUser(user);
        membership.setProject(projects);
        membership.setRole(roles);
        membership.setStatus(StaffStatus.ACTIVE);
        membership.setOwnership(true);

        membershipRepository.save(membership);
    }
    @Transactional
    public void addToMembership(Projects projects, Users users, Roles roles, Double wage, RoleCategory category){
        ProjectMembership membership = new ProjectMembership();
        membership.setUser(users);
        membership.setProject(projects);
        membership.setRole(roles);
        membership.setStatus(StaffStatus.ACTIVE);
        membership.setBaseDailyWage(wage);
        membership.setRoleCategory(category);

        membershipRepository.save(membership);
    }
}
