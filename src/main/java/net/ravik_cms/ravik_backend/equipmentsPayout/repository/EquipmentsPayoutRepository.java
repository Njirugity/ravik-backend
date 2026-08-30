package net.ravik_cms.ravik_backend.equipmentsPayout.repository;

import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.EquipmentsPayoutInfoProjection;
import net.ravik_cms.ravik_backend.equipmentsPayout.entity.EquipmentsPayout;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentsPayoutRepository extends JpaRepository<EquipmentsPayout, Long> {
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.equipmentsPayout.dtos.EquipmentsPayoutInfoProjection(
                            ep.id, ep.equipmentRequired.id, ep.equipmentRequired.equipments.id, ep.equipmentRequired.equipments.title, ep.equipmentRequired.equipments.category,
                            ep.workedDuration, ep.rentalCost, ep.fuelCost, ep.operatorCost, ep.dateUsed, ep.notes, ep.referenceCode,
                            ep.operatorCost + (CASE WHEN ep.equipmentRequired.equipments.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END),
                            ep.milestone.title, ep.paidAmount, ep.paymentStatus
                            )
                FROM EquipmentsPayout ep
                WHERE ep.milestone.id = :milestoneId
            """)
    List<EquipmentsPayoutInfoProjection> findAllByMilestoneId(@Param("milestoneId") UUID milestoneId);

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.equipmentsPayout.dtos.EquipmentsPayoutInfoProjection(
                            ep.id, ep.equipmentRequired.id, ep.equipmentRequired.equipments.id, ep.equipmentRequired.equipments.title, ep.equipmentRequired.equipments.category,
                            ep.workedDuration, ep.rentalCost, ep.fuelCost, ep.operatorCost, ep.dateUsed, ep.notes, ep.referenceCode,
                            ep.operatorCost + (CASE WHEN ep.equipmentRequired.equipments.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END),
                            ep.milestone.title, ep.paidAmount, ep.paymentStatus
                            )
                FROM EquipmentsPayout ep
                JOIN ep.equipmentRequired er
                JOIN er.equipments e
                WHERE ep.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
                AND (:dateUsed IS NULL OR ep.dateUsed = :dateUsed)
            """,
            countQuery = """
                SELECT COUNT(ep) FROM EquipmentsPayout ep
                JOIN ep.equipmentRequired er
                JOIN er.equipments e
                WHERE ep.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:category IS NULL OR e.category = :category)
                AND (:dateUsed IS NULL OR ep.dateUsed = :dateUsed)
            """
    )
    Page<EquipmentsPayoutInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("category") EquipmentCategory category,
            @Param("dateUsed") LocalDate dateUsed,
            Pageable pageable
    );

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection(
                            ep.id, ep.paymentCategory, ep.referenceCode, e.title, ep.project.id,
                            1.0 * (ep.operatorCost + (CASE WHEN e.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END)),
                            ep.paidAmount, ep.paymentStatus, ep.dateUsed)
                FROM EquipmentsPayout ep
                JOIN ep.equipmentRequired er
                JOIN er.equipments e
                WHERE ep.project.id = :projectId
                AND (:status IS NULL OR ep.paymentStatus = :status)
            """)
    List<PayoutSummaryProjection> findPayoutSummaries(@Param("projectId") UUID projectId, @Param("status") PaymentStatus status);

    boolean existsByReferenceCode(String referenceCode);

    @Query("""
                SELECT COALESCE(SUM(ep.operatorCost + (CASE WHEN ep.equipmentRequired.equipments.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END)), 0)
                FROM EquipmentsPayout ep
                WHERE ep.milestone.id = :milestoneId
            """)
    Double sumActualCostByMilestoneId(@Param("milestoneId") UUID milestoneId);

    @Query("""
                SELECT COALESCE(SUM(ep.operatorCost + (CASE WHEN ep.equipmentRequired.equipments.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END)), 0)
                FROM EquipmentsPayout ep
                WHERE ep.milestone.phase.id = :phaseId
            """)
    Double sumActualCostByPhaseId(@Param("phaseId") UUID phaseId);

    @Query("""
                SELECT COALESCE(SUM(ep.operatorCost + (CASE WHEN ep.equipmentRequired.equipments.category = net.ravik_cms.ravik_backend.common.enums.EquipmentCategory.OWNED
                                                     THEN ep.fuelCost
                                                     ELSE ep.rentalCost END)), 0)
                FROM EquipmentsPayout ep
                WHERE ep.project.id = :projectId
            """)
    Double sumActualCostByProjectId(@Param("projectId") UUID projectId);
}
