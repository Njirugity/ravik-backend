package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.repository;

import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.LaborRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.entity.LaborRequired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LaborRequiredRepository extends JpaRepository<LaborRequired, Long> {
    @Query("""
                SELECT new net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.LaborRequiredInfoProjection(
                            lr.id, lr.jobTitle.id, lr.jobTitle.title, lr.workersRequired)
                FROM LaborRequired lr
                WHERE lr.milestone.id = :milestoneId
            """)
    List<LaborRequiredInfoProjection> findAllByMilestoneId(@Param("milestoneId") UUID milestoneId);
}
