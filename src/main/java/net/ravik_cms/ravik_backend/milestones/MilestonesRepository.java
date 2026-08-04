package net.ravik_cms.ravik_backend.milestones;

import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
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
    List<Milestones> findAllByProjectId(UUID projectId);
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
        SELECT SUM(m.budget) FROM Milestones m
        WHERE m.phase.id = :phaseId
    """)
    Double phaseBudget(
            @Param("phaseId") UUID phaseId
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

}
