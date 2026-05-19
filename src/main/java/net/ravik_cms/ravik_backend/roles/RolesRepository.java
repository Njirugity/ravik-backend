package net.ravik_cms.ravik_backend.roles;

import net.ravik_cms.ravik_backend.common.enums.RoleCategory;
import net.ravik_cms.ravik_backend.projects.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolesRepository extends JpaRepository<Roles, UUID> {
    boolean existsByName(String name);
    Optional<Roles> findByName(String name);
    Optional<Roles> findByNameAndProject(String name, Projects project);
    Optional<Roles> findByIdAndProject(UUID id, Projects project);
    List<Roles> findAllByProject(Projects project);
    List<Roles> findAllByProjectAndRoleCategory(Projects projects, RoleCategory category);
}
