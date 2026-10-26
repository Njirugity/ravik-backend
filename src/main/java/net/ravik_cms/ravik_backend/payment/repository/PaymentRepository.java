package net.ravik_cms.ravik_backend.payment.repository;

import net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection;
import net.ravik_cms.ravik_backend.common.dtos.MonthAmountProjection;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.payment.dtos.PaymentInfoProjection;
import net.ravik_cms.ravik_backend.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.payment.dtos.PaymentInfoProjection(
                            p.id, p.datePaid, p.payee, p.amount, p.paymentCategory, p.referenceId, p.referenceCode, p.project.id, p.account.id, p.paymentStatus, p.transactionCode)
                FROM Payment p
                WHERE p.project.id = :projectId
                AND (:category IS NULL OR p.paymentCategory = :category)
                AND (:status IS NULL OR p.paymentStatus = :status)
                AND (:datePaid IS NULL OR p.datePaid = :datePaid)
            """,
            countQuery = """
                SELECT COUNT(p) FROM Payment p
                WHERE p.project.id = :projectId
                AND (:category IS NULL OR p.paymentCategory = :category)
                AND (:status IS NULL OR p.paymentStatus = :status)
                AND (:datePaid IS NULL OR p.datePaid = :datePaid)
                """
    )
    Page<PaymentInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("category") PaymentCategory category,
            @Param("status") PaymentStatus status,
            @Param("datePaid") LocalDate datePaid,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.project.id = :projectId")
    Double sumAmountByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.project.id = :projectId AND p.paymentCategory IN :categories")
    Double sumAmountByProjectIdAndPaymentCategoryIn(
            @Param("projectId") UUID projectId,
            @Param("categories") Collection<PaymentCategory> categories
    );

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.account.id = :accountId")
    Double sumAmountByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.project.id = :projectId AND p.account.id = :accountId")
    Double sumAmountByProjectIdAndAccountId(@Param("projectId") UUID projectId, @Param("accountId") UUID accountId);

    /**
     * Expense payments re-bucketed into the budget category of the expense they settle.
     * Payments against an expense without a category are left out.
     */
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            c.budgetCategory, COALESCE(SUM(p.amount), 0))
                FROM Payment p
                JOIN Expenses e ON e.id = p.referenceId
                JOIN e.category c
                WHERE p.project.id = :projectId
                AND p.paymentCategory = net.ravik_cms.ravik_backend.common.enums.PaymentCategory.EXPENSES
                GROUP BY c.budgetCategory
            """)
    List<CategoryAmountProjection> sumExpensePaymentsByBudgetCategory(@Param("projectId") UUID projectId);

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.MonthAmountProjection(
                            YEAR(p.datePaid), MONTH(p.datePaid), COALESCE(SUM(p.amount), 0))
                FROM Payment p
                WHERE p.project.id = :projectId
                AND p.datePaid >= :from
                GROUP BY YEAR(p.datePaid), MONTH(p.datePaid)
            """)
    List<MonthAmountProjection> sumByProjectGroupByMonth(@Param("projectId") UUID projectId, @Param("from") LocalDate from);
}
