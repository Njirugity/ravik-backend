package net.ravik_cms.ravik_backend.expenseCategory.repository;

import net.ravik_cms.ravik_backend.expenseCategory.dto.ExpenseCategoryInfoProjection;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.expenseCategory.dto.ExpenseCategoryInfoProjection(
                            ec.id, ec.title, ec.budgetCategory)
                FROM ExpenseCategory ec
                WHERE ec.projects.id = :projectId
                AND (:search IS NULL OR LOWER(ec.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
                SELECT COUNT(ec) FROM ExpenseCategory ec
                WHERE ec.projects.id = :projectId
                AND (:search IS NULL OR LOWER(ec.title) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<ExpenseCategoryInfoProjection> findAllByProject(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );
}
