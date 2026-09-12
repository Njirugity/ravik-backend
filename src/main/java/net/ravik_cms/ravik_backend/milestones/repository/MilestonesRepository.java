package net.ravik_cms.ravik_backend.milestones.repository;

import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
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
public interface MilestonesRepository extends JpaRepository<Milestones, UUID> {
    Optional<Milestones> findByIdAndProjectId(UUID id, UUID projectId);
    List<Milestones> findAllByProjectId(UUID projectId);
    @Query(
            value = """
                SELECT m FROM Milestones m
                WHERE m.project.id = :projectId
                AND (:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')))
                ORDER BY m.earliestStart ASC
            """,
            countQuery = """
                SELECT COUNT(m) FROM Milestones m
                WHERE m.project.id = :projectId
                AND (:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<Milestones> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );
    List<Milestones> findAllByPhaseId(UUID phaseId);
    boolean existsByPhaseIdAndStatusNotAndEarliestFinishBefore(
            UUID phaseId, ProgressStatus status, LocalDate currentDate);
    @Query("SELECT MIN(m.actualStartDate) FROM Milestones m WHERE m.phase.id = :phaseId")
    Optional<LocalDate> findMinActualStartDateByPhaseId(@Param("phaseId") UUID phaseId);
    @Query("SELECT COUNT(m) FROM Milestones m WHERE m.phase.id = :phaseId")
    Long countByPhaseId(@Param("phaseId") UUID phaseId);
    @Query("SELECT COUNT(m) FROM Milestones m WHERE m.phase.id = :phaseId AND m.status = 'COMPLETED'")
    Long countByPhaseIdAndCompleted(@Param("phaseId") UUID phaseId);

    Optional<Milestones> findByTitleIgnoreCase(String title);
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND :currentDate BETWEEN m.earliestStart AND m.earliestFinish
        AND m.status IN ('PENDING','IN_PROGRESS')
        ORDER BY m.earliestFinish ASC
    """)
    List<Milestones> findActiveMilestoneByDate(
            @Param("projectId") UUID projectId,
            @Param("currentDate") LocalDate currentDate
    );
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.earliestFinish < :currentDate
        AND m.status IN ('PENDING','IN_PROGRESS')
    """)
    List<Milestones> findOverdueMilestones(
            @Param("projectId") UUID projectId,
            @Param("currentDate") LocalDate currentDate
    );
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.earliestFinish BETWEEN :currentDate AND :thresholdDate
        AND m.status IN ('PENDING','IN_PROGRESS')
    """)
    List<Milestones> findAlmostDueMilestones(
            @Param("projectId") UUID projectId,
            @Param("currentDate") LocalDate currentDate,
            @Param("thresholdDate") LocalDate thresholdDate
    );
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.actualStartDate IS NULL
        AND :date BETWEEN m.earliestStart AND m.latestFinish
    """)
    List<Milestones> findByActualStartDateIsNullAndDateBetweenESAnsLF(
            @Param("date") LocalDate date,
            @Param("projectId") UUID projectId
    );
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND :date BETWEEN m.earliestStart AND m.latestFinish
    """)
    List<Milestones> findByDateBetweenESAndLF(
            @Param("date") LocalDate date,
            @Param("projectId") UUID projectId
    );
    @Query("""
        SELECT m FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.actualStartDate IS NULL
        AND NOT EXISTS(SELECT md FROM MilestoneDependency md
            WHERE md.milestone = m)
    """)
    List<Milestones> findWithNoPredecessorsAndNoActualStart(
            @Param("projectId") UUID projectId
    );

    long countByProjectId(UUID projectId);
    long countByProjectIdAndStatus(UUID projectId, ProgressStatus status);
    @Query("""
        SELECT COUNT(m) FROM Milestones m
        WHERE m.project.id = :projectId
        AND m.earliestFinish < :currentDate
        AND m.status IN ('PENDING','IN_PROGRESS')
    """)
    long countOverdueMilestones(
            @Param("projectId") UUID projectId,
            @Param("currentDate") LocalDate currentDate
    );
}
