package net.ravik_cms.ravik_backend.materials.repository;

import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredAggregateProjection;
import net.ravik_cms.ravik_backend.materials.entity.MaterialDelivered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialDeliveredRepository extends JpaRepository<MaterialDelivered, UUID> {
    Optional<MaterialDelivered> findByIdAndProjectIdAndStatus(UUID id, UUID projectId, DeletionStatus status);
    List<MaterialDelivered> findAllByProjectIdAndStatus(UUID projectId, DeletionStatus status);
    List<MaterialDelivered> findAllByProjectIdAndMaterialListIdAndStatus(UUID projectId, UUID materialListId, DeletionStatus status);

    @Query("""
        SELECT COALESCE(SUM(md.quantityDelivered), 0)
        FROM MaterialDelivered md
        WHERE md.project.id = :projectId AND md.materialList.id = :materialListId AND md.status = 'NOT_DELETED'
    """)
    Double sumDeliveredQuantity(@Param("projectId") UUID projectId, @Param("materialListId") UUID materialListId);

    @Query("""
        SELECT new net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredAggregateProjection(
            md.materialList.id, SUM(md.quantityDelivered), SUM(md.quantityDelivered * md.unitPrice))
        FROM MaterialDelivered md
        WHERE md.project.id = :projectId AND md.status = 'NOT_DELETED'
        GROUP BY md.materialList.id
    """)
    List<MaterialDeliveredAggregateProjection> aggregateByMaterialForProject(@Param("projectId") UUID projectId);
}
