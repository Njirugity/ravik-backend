package net.ravik_cms.ravik_backend.phase.repository;

import net.ravik_cms.ravik_backend.phase.entity.Phases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhasesRepository extends JpaRepository<Phases, UUID> {
    Optional<Phases> findByProjectIdAndId(UUID projectId, UUID phaseId);
    Optional<Phases> findByTitle(UUID phaseId);

    @Query(
            value = """
                SELECT p FROM Phases p
                WHERE p.project.id = :projectId
                AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
                SELECT COUNT(p) FROM Phases p
                WHERE p.project.id = :projectId
                AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<Phases> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT m.phase FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.status = 'IN_PROGRESS'
        ORDER BY m.phase.plannedStartDate ASC
    """)
    List<Phases> findActivePhases(@Param("projectId") UUID projectId);
    @Query("SELECT MAX(p.plannedEndDate) FROM Phases p WHERE p.project.id = :projectId")
    Optional<LocalDate> findProjectPlannedEndDate(@Param("projectId") UUID projectId);
    @Query("SELECT COUNT(p) FROM Phases p WHERE p.project.id = :projectId")
    Optional<Long> countByProjectId(@Param("projectId") UUID projectId);
}
