package net.ravik_cms.ravik_backend.attendance;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query("""
            SELECT m FROM ProjectMembership m
            LEFT JOIN Attendance a ON a.membership = m AND a.date = :date
            WHERE m.project.id = :projectId
            AND m.status = 'ACTIVE'
            AND a.id IS NULL
            """)
    List<ProjectMembership> findMembershipsWithoutAttendance(
            @Param("projectId") UUID projectId,
            @Param("date") LocalDate date
    );
    @Query("""
          SELECT a FROM Attendance a
          JOIN FETCH a.membership m
          JOIN FETCH m.user u
          WHERE a.date = :date
          AND m.project.id = :projectId
          AND m.status = 'ACTIVE'
    """)
    List<Attendance> findByProjectIdAndDate(
            @Param("projectId") UUID projectId,
            @Param("date") LocalDate date
    );
    @Query("SELECT a FROM Attendance a " +
            "JOIN FETCH a.membership m " +
            "JOIN FETCH m.user u " +
            "WHERE m.project.id = :projectId " +
            "AND a.date BETWEEN :start AND :end")
    List<Attendance> findAttendanceForSummary(
            @Param("projectId") UUID projectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

}
