package net.ravik_cms.ravik_backend.permission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
    boolean existsByName(String name);
    Permissions findByName(String name);
    Set<Permissions> findByNameIn(Collection<String> names);
}
