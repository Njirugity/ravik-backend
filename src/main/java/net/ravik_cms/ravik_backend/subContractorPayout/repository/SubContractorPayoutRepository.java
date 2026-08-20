package net.ravik_cms.ravik_backend.subContractorPayout.repository;

import net.ravik_cms.ravik_backend.subContractorPayout.dtos.SubContractorPayoutInfoProjection;
import net.ravik_cms.ravik_backend.subContractorPayout.entity.SubContractorPayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface SubContractorPayoutRepository extends JpaRepository<SubContractorPayout, Long> {
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.subContractorPayout.dtos.SubContractorPayoutInfoProjection(
                            sp.id, sp.subContractorRequired.id, sp.subContractorRequired.subContractor.id, sp.subContractorRequired.subContractor.title,
                            sp.actualJobCost, sp.paidAmount, sp.jobDate, sp.milestone.title)
                FROM SubContractorPayout sp
                WHERE sp.milestone.id = :milestoneId
            """)
    List<SubContractorPayoutInfoProjection> findAllByMilestoneId(@Param("milestoneId") UUID milestoneId);

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.subContractorPayout.dtos.SubContractorPayoutInfoProjection(
                            sp.id, sp.subContractorRequired.id, sc.id, sc.title,
                            sp.actualJobCost, sp.paidAmount, sp.jobDate, sp.milestone.title)
                FROM SubContractorPayout sp
                JOIN sp.subContractorRequired sr
                JOIN sr.subContractor sc
                WHERE sp.project.id = :projectId
                AND (:search IS NULL OR LOWER(sc.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobDate IS NULL OR sp.jobDate = :jobDate)
            """,
            countQuery = """
                SELECT COUNT(sp) FROM SubContractorPayout sp
                JOIN sp.subContractorRequired sr
                JOIN sr.subContractor sc
                WHERE sp.project.id = :projectId
                AND (:search IS NULL OR LOWER(sc.title) LIKE LOWER(CONCAT('%', :search, '%')))
                AND (:jobDate IS NULL OR sp.jobDate = :jobDate)
            """
    )
    Page<SubContractorPayoutInfoProjection> findAllByProjectId(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            @Param("jobDate") LocalDate jobDate,
            Pageable pageable
    );
}
