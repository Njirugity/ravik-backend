package net.ravik_cms.ravik_backend.subContractor.repository;

import net.ravik_cms.ravik_backend.subContractor.dtos.SubContractorInfoProjection;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubContractorRepository extends JpaRepository<SubContractor, Long> {
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.subContractor.dtos.SubContractorInfoProjection(
                            s.id, s.title, s.jobType, s.email, s.address, s.phoneNumber)
                FROM SubContractor s
                WHERE s.project.id = :projectId
                AND (:search IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobType IS NULL OR LOWER(s.jobType) = LOWER(:jobType))
            """,
            countQuery = """
                SELECT COUNT(s) FROM SubContractor s
                WHERE s.project.id = :projectId
                AND (:search IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobType IS NULL OR LOWER(s.jobType) = LOWER(:jobType))
                """
    )
    Page<SubContractorInfoProjection> findAllSubContractors(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("jobType") String jobType,
            Pageable pageable
    );
}
