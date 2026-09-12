package net.ravik_cms.ravik_backend.income.repository;

import net.ravik_cms.ravik_backend.income.dtos.IncomeInfoProjection;
import net.ravik_cms.ravik_backend.income.entity.Income;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IncomeRepository extends JpaRepository<Income, UUID> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.income.dtos.IncomeInfoProjection(
                            i.id, i.source, i.description, i.amount, i.date)
                FROM Income i
                WHERE i.project.id = :projectId
                AND (:search IS NULL OR LOWER(i.source) LIKE LOWER(CONCAT('%', :search, '%'))
                     OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
                SELECT COUNT(i) FROM Income i
                WHERE i.project.id = :projectId
                AND (:search IS NULL OR LOWER(i.source) LIKE LOWER(CONCAT('%', :search, '%'))
                     OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<IncomeInfoProjection> findAllByProject(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.project.id = :projectId")
    Double sumAmountByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.account.id = :accountId")
    Double sumAmountByAccountId(@Param("accountId") UUID accountId);

    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM Income i WHERE i.project.id = :projectId AND i.account.id = :accountId")
    Double sumAmountByProjectIdAndAccountId(@Param("projectId") UUID projectId, @Param("accountId") UUID accountId);
}
