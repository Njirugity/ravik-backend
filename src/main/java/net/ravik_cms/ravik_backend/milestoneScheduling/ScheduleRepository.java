package net.ravik_cms.ravik_backend.milestoneScheduling;

import jakarta.transaction.Transactional;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<MilestoneDependency, UUID> {
    //find all predecessors of a milestone
    @Query("""
        SELECT md.predecessor
        FROM MilestoneDependency md
        WHERE md.milestone.id = :milestoneId
        """)
    List<Milestones> findPredecessorByMilestoneId(@Param("milestoneId") UUID milestoneId);

    //find all successors of a milestone
    @Query("""
        SELECT md.milestone
        FROM MilestoneDependency md
        WHERE md.predecessor.id = :milestoneId
        """)
    List<Milestones> findSuccessorByMilestoneId(@Param("milestoneId") UUID milestoneId);

    List<MilestoneDependency> findByMilestoneIdIn(List<UUID> milestoneId);

    List<MilestoneDependency> findByProjectId(UUID projectId);
    //Check if relationship exits
    boolean existsByMilestoneIdAndPredecessorId(UUID milestoneId, UUID predecessorId);
    //delete a relationship
    @Transactional
    void deleteByMilestoneIdAndPredecessorId(UUID milestoneId, UUID predecessorId);
    @Transactional
    void deleteByMilestoneId(UUID milestoneId);
    @Transactional
    void deleteByPredecessorId(UUID predecessorId);
}
