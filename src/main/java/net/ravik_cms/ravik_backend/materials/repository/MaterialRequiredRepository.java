package net.ravik_cms.ravik_backend.materials.repository;

import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredAggregateProjection;
import net.ravik_cms.ravik_backend.materials.entity.MaterialRequired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRequiredRepository extends JpaRepository<MaterialRequired, UUID> {
    Optional<MaterialRequired> findByIdAndProjectId(UUID id, UUID projectId);
    List<MaterialRequired> findAllByProjectIdAndMilestoneId(UUID projectId, UUID milestoneId);
    List<MaterialRequired> findAllByProjectId(UUID projectId);

    @Query("""
        SELECT COALESCE(SUM(mr.quantityRequired * mr.unitPrice), 0)
        FROM MaterialRequired mr
        WHERE mr.project.id = :projectId AND mr.milestone.id = :milestoneId
    """)
    Double sumPlannedCostByMilestone(@Param("projectId") UUID projectId, @Param("milestoneId") UUID milestoneId);

    @Query("""
        SELECT COALESCE(SUM(mr.quantityRequired * mr.unitPrice), 0)
        FROM MaterialRequired mr
        WHERE mr.project.id = :projectId
    """)
    Double sumPlannedCostByProject(@Param("projectId") UUID projectId);

    @Query("""
        SELECT new net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredAggregateProjection(
            mr.materialList.id, SUM(mr.quantityRequired), SUM(mr.quantityRequired * mr.unitPrice))
        FROM MaterialRequired mr
        WHERE mr.project.id = :projectId
        GROUP BY mr.materialList.id
    """)
    List<MaterialRequiredAggregateProjection> aggregateByMaterialForProject(@Param("projectId") UUID projectId);
}
