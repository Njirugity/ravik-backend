package net.ravik_cms.ravik_backend.budget.repository;

import net.ravik_cms.ravik_backend.budget.dtos.BudgetInfoProjection;
import net.ravik_cms.ravik_backend.budget.entity.Budget;
import net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.budget.dtos.BudgetInfoProjection(
                            b.id, b.amount, b.category, b.source)
                FROM Budget b
                WHERE b.projects.id = :projectId
                AND (:category IS NULL OR b.category = :category)
            """,
            countQuery = """
                SELECT COUNT(b) FROM Budget b
                WHERE b.projects.id = :projectId
                AND (:category IS NULL OR b.category = :category)
            """
    )
    Page<BudgetInfoProjection> findAllByProject(
            @Param("projectId") UUID projectId,
            @Param("category") BudgetCategory category,
            Pageable pageable
    );

    @Query("""
                SELECT new net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection(
                            b.category, COALESCE(SUM(b.amount), 0))
                FROM Budget b
                WHERE b.projects.id = :projectId
                GROUP BY b.category
            """)
    List<CategoryAmountProjection> sumApprovedBudgetByProjectGroupByCategory(@Param("projectId") UUID projectId);
}
