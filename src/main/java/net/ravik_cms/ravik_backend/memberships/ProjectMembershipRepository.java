package net.ravik_cms.ravik_backend.memberships;

import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    List<ProjectMembership> findAllByUser(Users user);
    List<ProjectMembership> findAllByProject(Projects project);
    List<ProjectMembership> findAllByProjectAndRole(Projects project, Roles role);
    Optional<ProjectMembership> findByUserAndProject(Users user, Projects project);
    List<ProjectMembership> findAllByProjectIdAndStatus(UUID projectId, String status);
    Optional <ProjectMembership> findByProjectAndStatusAndId(Projects project, String status,Long id);
    List<ProjectMembership> findAllByProjectId(UUID projectId);
}
