package net.ravik_cms.ravik_backend.materials.repository;

import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedAggregateProjection;
import net.ravik_cms.ravik_backend.materials.entity.MaterialUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialUsedRepository extends JpaRepository<MaterialUsed, UUID> {
    Optional<MaterialUsed> findByIdAndProjectId(UUID id, UUID projectId);
    List<MaterialUsed> findAllByProjectIdAndMilestoneId(UUID projectId, UUID milestoneId);
    List<MaterialUsed> findAllByProjectId(UUID projectId);

    @Query("""
        SELECT COALESCE(SUM(mu.quantityUsed), 0)
        FROM MaterialUsed mu
        WHERE mu.project.id = :projectId AND mu.materialDelivered.materialList.id = :materialListId
    """)
    Double sumUsedQuantity(@Param("projectId") UUID projectId, @Param("materialListId") UUID materialListId);

    @Query("""
        SELECT new net.ravik_cms.ravik_backend.materials.dto.MaterialUsedAggregateProjection(
            mu.materialDelivered.materialList.id, SUM(mu.quantityUsed))
        FROM MaterialUsed mu
        WHERE mu.project.id = :projectId
        GROUP BY mu.materialDelivered.materialList.id
    """)
    List<MaterialUsedAggregateProjection> aggregateByMaterialForProject(@Param("projectId") UUID projectId);
}
