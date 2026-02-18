package net.ravik_cms.ravik_backend.wages;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WagesRepository extends JpaRepository<Wages, Long> {
    boolean existsByMembershipAndStartDateAndEndDate(ProjectMembership membership, LocalDate start, LocalDate end);
    List<Wages> findAllByMembershipInAndStartDateAndEndDate(List<ProjectMembership> membership, LocalDate start, LocalDate end);
}
