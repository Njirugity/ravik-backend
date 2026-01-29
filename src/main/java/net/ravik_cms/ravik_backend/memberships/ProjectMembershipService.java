package net.ravik_cms.ravik_backend.memberships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectMembershipService {
    private final ProjectMembershipRepository membershipRepository;

    public void createOwnerMembership(Users user, Projects projects, Roles roles){
        ProjectMembership membership = new ProjectMembership();
        membership.setUser(user);
        membership.setProject(projects);
        membership.setRole(roles);
        membership.setStatus("ACTIVE");
        membership.setOwnership(true);

        membershipRepository.save(membership);
    }
    @Transactional
    public void addToMembership(Projects projects, Users users, Roles roles){
        ProjectMembership membership = new ProjectMembership();
        membership.setUser(users);
        membership.setProject(projects);
        membership.setRole(roles);
        membership.setStatus("ACTIVE");

        membershipRepository.save(membership);
    }
}
