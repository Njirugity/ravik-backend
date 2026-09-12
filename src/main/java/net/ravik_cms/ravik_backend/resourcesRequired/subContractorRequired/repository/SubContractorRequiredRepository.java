package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.repository;

import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubContractorRequiredRepository extends JpaRepository<SubContractorRequired, Long> {
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection(
                            sr.id, sr.subContractor.id, sr.subContractor.title, sr.milestone.id, sr.dateRequired, sr.jobAmount, sr.jobTitle, sr.description)
                FROM SubContractorRequired sr
                WHERE sr.milestone.id = :milestoneId
            """)
    List<SubContractorRequiredInfoProjection> findAllByMilestoneId(@Param("milestoneId") UUID milestoneId);

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection(
                            sr.id, sc.id, sc.title, sr.milestone.id, sr.dateRequired, sr.jobAmount, sr.jobTitle, sr.description)
                FROM SubContractorRequired sr
                JOIN sr.subContractor sc
                WHERE sr.project.id = :projectId
                AND (:search IS NULL OR LOWER(sc.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobType IS NULL OR LOWER(sc.jobType) = LOWER(:jobType))
            """,
            countQuery = """
                SELECT COUNT(sr) FROM SubContractorRequired sr
                JOIN sr.subContractor sc
                WHERE sr.project.id = :projectId
                AND (:search IS NULL OR LOWER(sc.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobType IS NULL OR LOWER(sc.jobType) = LOWER(:jobType))
            """
    )
    Page<SubContractorRequiredInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("jobType") String jobType,
            Pageable pageable
    );
}
