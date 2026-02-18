package net.ravik_cms.ravik_backend.attendance;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByMembershipAndDate(ProjectMembership membership, LocalDate date);

    List<Attendance> findByMembershipInAndDateBetween(
            List<ProjectMembership> memberships,
            LocalDate start,
            LocalDate end
    );

    Optional<Attendance> findByMembershipAndId(ProjectMembership membership, Long id);
    List<Attendance> findByMembershipAndPresentTrueAndLockedFalseAndDateBetween(ProjectMembership membership, LocalDate start, LocalDate end);
}
