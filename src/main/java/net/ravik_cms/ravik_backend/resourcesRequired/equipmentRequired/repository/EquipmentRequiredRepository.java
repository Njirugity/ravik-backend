package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.repository;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.EquipmentRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentRequiredRepository extends JpaRepository<EquipmentRequired, Long> {
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.
                            EquipmentRequiredInfoProjection(er.id, er.equipments.id, er.equipments.title,
                                        er.equipments.category, er.milestone.id,
                                        er.plannedDuration, er.plannedFuelCost, er.notes, er.dateRequired)
                FROM EquipmentRequired er
                WHERE er.milestone.id = :milestoneId
            """)
    List<EquipmentRequiredInfoProjection> findAllByMilestoneId(@Param("milestoneId") UUID milestoneId);
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.
                            EquipmentRequiredInfoProjection(er.id, er.equipments.id, er.equipments.title,
                                        e.category, er.milestone.id,
                                        er.plannedDuration, er.plannedFuelCost, er.notes, er.dateRequired)
                FROM EquipmentRequired er
                JOIN er.equipments e
                WHERE er.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
            """,
            countQuery = """
                SELECT COUNT(er) FROM EquipmentRequired er
                JOIN er.equipments e
                WHERE er.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
            """
    )
    Page<EquipmentRequiredInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("category") EquipmentCategory category,
            Pageable pageable
    );
}
