package net.ravik_cms.ravik_backend.jobTitles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JobTitlesRepository extends JpaRepository<JobTitles, Long> {
    List<JobTitles> findAllByProjectId(UUID projectId);
    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.jobTitles.JobTitlesInfoProjection(
                            j.id, j.title, j.baseWage, j.frequency, COUNT(m))
                FROM JobTitles j
                LEFT JOIN ProjectMembership m
                    ON m.jobTitle = j
                WHERE j.project.id = :projectId
                AND (
                     :search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                GROUP BY j.id, j.title, j.baseWage, j.frequency
            """,
            countQuery = """
                SELECT COUNT(j) FROM JobTitles j
                LEFT JOIN ProjectMembership m
                    ON m.jobTitle = j
                WHERE j.project.id = :projectId
                AND (
                     :search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                """
    )
    Page<JobTitlesInfoProjection> findAllJobTitles(
            @Param("projectId") UUID projectId,
            @Param("search") String search,
            Pageable pageable
    );
}
