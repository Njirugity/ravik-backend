package net.ravik_cms.ravik_backend.roles;

import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.projects.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID> {
    Optional<Roles> findByNameAndProject(String name, Projects project);
    Optional<Roles> findByIdAndProject(UUID id, Projects project);
    List<Roles> findAllByProject(Projects project);

    @Query("SELECT m.role.id AS roleId, COUNT(m) AS memberCount FROM ProjectMembership m " +
            "WHERE m.role.project.id = :projectId GROUP BY m.role.id")
    List<RoleMembershipCount> countMembersByRole(@Param("projectId") UUID projectId);
}
