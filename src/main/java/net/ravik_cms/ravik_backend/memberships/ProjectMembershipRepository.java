package net.ravik_cms.ravik_backend.memberships;

import jakarta.persistence.Cacheable;
import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.common.enums.StaffStatus;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.roles.Roles;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    List<ProjectMembership> findAllByUser(Users user);
    List<ProjectMembership> findAllByProject(Projects project);
    List<ProjectMembership> findAllByProjectAndRole(Projects project, Roles role);
    List<ProjectMembership> findAllByProjectAndRoleCategory(Projects projects, RoleCategory category);
    Optional<ProjectMembership> findByUserAndProject(Users user, Projects project);

    @Query("""
        SELECT m FROM ProjectMembership m
        JOIN FETCH m.role r
        JOIN FETCH r.permissions
        WHERE m.user.id = :userId
        AND m.project.id = :projectId
    """)
    Optional<ProjectMembership> findByUserIdAndProjectId(
            @Param("projectId") UUID projectId,
            @Param("userId") UUID userId);
    @Query("""
        SELECT m FROM ProjectMembership m
        WHERE m.user.id = :userId
        AND m.role.name = :roleName
    """)
    Optional<ProjectMembership> findByUserIdAndRoleName(
            @Param("roleName") String roleName,
            @Param("userId") UUID userId);
    List<ProjectMembership> findAllByProjectIdAndStatus(UUID projectId, StaffStatus status);

    Optional <ProjectMembership> findByProjectAndStatusAndId(Projects project, String status,Long id);
    List<ProjectMembership> findAllByProjectId(UUID projectId);
}
