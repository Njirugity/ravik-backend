package net.ravik_cms.ravik_backend.projects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, UUID> {
    Optional <Projects> findByIdAndClientId(UUID id,UUID clientId);
    List<Projects> findAllByClientId(UUID clientId);
}
