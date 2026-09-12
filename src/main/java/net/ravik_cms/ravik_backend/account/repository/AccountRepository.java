package net.ravik_cms.ravik_backend.account.repository;

import net.ravik_cms.ravik_backend.account.dtos.AccountInfoProjection;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Accounts, UUID> {
    List<Accounts> findAllByClientId(UUID clientId);

    @Query("""
                SELECT DISTINCT a FROM Accounts a
                WHERE a.id IN (SELECT i.account.id FROM Income i WHERE i.project.id = :projectId)
                OR a.id IN (SELECT p.account.id FROM Payment p WHERE p.project.id = :projectId AND p.account IS NOT NULL)
            """)
    List<Accounts> findAllUsedInProject(@Param("projectId") UUID projectId);

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.account.dtos.AccountInfoProjection(
                            a.id, a.name, a.type, a.openingBalance)
                FROM Accounts a
                WHERE a.client.id = :clientId
                AND (:search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
                SELECT COUNT(a) FROM Accounts a
                WHERE a.client.id = :clientId
                AND (:search IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """
    )
    Page<AccountInfoProjection> findAllByClient(
            @Param("clientId") UUID clientId,
            @Param("search") String search,
            Pageable pageable
    );
}
