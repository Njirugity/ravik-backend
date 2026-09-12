package net.ravik_cms.ravik_backend.milestoneBudget.repository;

import net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection;
import net.ravik_cms.ravik_backend.milestoneBudget.entity.MilestoneBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MilestoneBudgetRepository extends JpaRepository<MilestoneBudget, Long> {
    List<MilestoneBudget> findAllByMilestoneId(UUID milestoneId);

    void deleteAllByMilestoneId(UUID milestoneId);

    @Query("SELECT COALESCE(SUM(mb.amount), 0) FROM MilestoneBudget mb WHERE mb.milestone.id = :milestoneId")
    Double sumAmountByMilestoneId(@Param("milestoneId") UUID milestoneId);

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            mb.category, COALESCE(SUM(mb.amount), 0))
                FROM MilestoneBudget mb
                WHERE mb.milestone.id = :milestoneId
                GROUP BY mb.category
            """)
    List<CategoryAmountProjection> sumByMilestoneGroupByCategory(@Param("milestoneId") UUID milestoneId);

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            mb.category, COALESCE(SUM(mb.amount), 0))
                FROM MilestoneBudget mb
                WHERE mb.milestone.phase.id = :phaseId
                GROUP BY mb.category
            """)
    List<CategoryAmountProjection> sumByPhaseGroupByCategory(@Param("phaseId") UUID phaseId);

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            mb.category, COALESCE(SUM(mb.amount), 0))
                FROM MilestoneBudget mb
                WHERE mb.milestone.project.id = :projectId
                GROUP BY mb.category
            """)
    List<CategoryAmountProjection> sumByProjectGroupByCategory(@Param("projectId") UUID projectId);
}
