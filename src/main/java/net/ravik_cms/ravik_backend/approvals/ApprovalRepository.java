package net.ravik_cms.ravik_backend.approvals;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    @Query(
            value = """
                SELECT a FROM Approval a
                WHERE a.project.id = :projectId
                AND (
                     :search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
            """,
            countQuery = """
                SELECT COUNT(a) FROM Approval a
                WHERE a.project.id = :projectId
                AND (
                     :search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                """
    )
    Page<Approval> findAllApprovals(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );
}
