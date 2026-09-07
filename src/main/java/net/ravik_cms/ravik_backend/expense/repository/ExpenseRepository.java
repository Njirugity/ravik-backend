package net.ravik_cms.ravik_backend.expense.repository;

import net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.expense.dtos.ExpenseInfoProjection;
import net.ravik_cms.ravik_backend.expense.entity.Expenses;
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
public interface ExpenseRepository extends JpaRepository<Expenses, UUID> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.expense.dtos.ExpenseInfoProjection(
                            e.id, e.title, e.description,e.referenceCode, e.amount, e.paidAmount, e.paymentStatus, e.date, e.category.id, e.category.title)
                FROM Expenses e
                WHERE e.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
                SELECT COUNT(e) FROM Expenses e
                WHERE e.project.id = :projectId
                AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<ExpenseInfoProjection> findAllByProject(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            e.category.budgetCategory, COALESCE(SUM(e.amount), 0))
                FROM Expenses e
                WHERE e.project.id = :projectId
                GROUP BY e.category.budgetCategory
            """)
    List<CategoryAmountProjection> sumByProjectGroupByBudgetCategory(@Param("projectId") UUID projectId);
    boolean existsByReferenceCode(String referenceCode);

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection(
                            e.id, e.paymentCategory, e.referenceCode, e.title, e.project.id,
                            e.amount, e.paidAmount, e.paymentStatus, e.date)
                FROM Expenses e
                WHERE e.project.id = :projectId
                AND (:status IS NULL OR e.paymentStatus = :status)
            """)
    List<PayoutSummaryProjection> findPayoutSummaries(@Param("projectId") UUID projectId, @Param("status") PaymentStatus status);
}
