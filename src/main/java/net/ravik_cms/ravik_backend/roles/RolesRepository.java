package net.ravik_cms.ravik_backend.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    boolean existsByName(String name);
    Optional<Roles> findByName(String name);
}
