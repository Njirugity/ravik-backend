package net.ravik_cms.ravik_backend.equipment.repository;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.equipment.dtos.EquipmentInfoProjection;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipments, Long> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.equipment.dtos.EquipmentInfoProjection(
                            e.id, e.title, e.category, e.capacity, e.metric)
                FROM Equipments e
                WHERE e.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
            """,
            countQuery = """
                SELECT COUNT(e) FROM Equipments e
                WHERE e.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
            """
    )
    Page<EquipmentInfoProjection> findAllEquipment(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("category") EquipmentCategory category,
            Pageable pageable
    );
}
