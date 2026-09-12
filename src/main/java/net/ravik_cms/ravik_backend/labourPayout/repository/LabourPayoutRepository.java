package net.ravik_cms.ravik_backend.labourPayout.repository;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.labourPayout.dtos.LabourPayoutInfoProjection;
import net.ravik_cms.ravik_backend.labourPayout.entity.LabourPayout;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LabourPayoutRepository extends JpaRepository<LabourPayout, UUID> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.labourPayout.dtos.LabourPayoutInfoProjection(
                            lp.id, lp.periodStart, lp.periodEnd, lp.totalAmount, lp.referenceCode, lp.paidAmount, lp.paymentStatus)
                FROM LabourPayout lp
                WHERE lp.projectId = :projectId
                AND (:paymentStatus IS NULL OR lp.paymentStatus = :paymentStatus)
            """,
            countQuery = """
                SELECT COUNT(lp) FROM LabourPayout lp
                WHERE lp.projectId = :projectId
                AND (:paymentStatus IS NULL OR lp.paymentStatus = :paymentStatus)
                """
    )
    Page<LabourPayoutInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            Pageable pageable
    );

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection(
                            lp.id, lp.paymentCategory, lp.referenceCode, 'Labour Payout', lp.projectId,
                            lp.totalAmount, lp.paidAmount, lp.paymentStatus, lp.periodEnd)
                FROM LabourPayout lp
                WHERE lp.projectId = :projectId
                AND (:status IS NULL OR lp.paymentStatus = :status)
            """)
    List<PayoutSummaryProjection> findPayoutSummaries(@Param("projectId") UUID projectId, @Param("status") PaymentStatus status);

    boolean existsByReferenceCode(String referenceCode);

    @Query("SELECT COALESCE(SUM(lp.totalAmount), 0) FROM LabourPayout lp WHERE lp.projectId = :projectId")
    Double sumTotalAmountByProjectId(@Param("projectId") UUID projectId);
}
