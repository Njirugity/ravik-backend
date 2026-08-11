package net.ravik_cms.ravik_backend.attendance;

import jakarta.persistence.LockModeType;
import net.ravik_cms.ravik_backend.common.enums.AttendanceStatus;
import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
import net.ravik_cms.ravik_backend.wages.Wages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Query(
            value= """
            SELECT m FROM ProjectMembership m
            LEFT JOIN Attendance a ON a.membership = m AND a.date = :date
            JOIN m.user u
            JOIN m.jobTitle j
            WHERE m.project.id = :projectId
            AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
            AND m.generateAttendance = true
            AND a.id IS NULL
            AND (:jobTitle IS NULL OR j.title = :jobTitle)
            AND (
                   :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                 )
            """,
            countQuery = """
                    SELECT COUNT(m) FROM ProjectMembership m
                    LEFT JOIN Attendance a ON a.membership = m AND a.date = :date
                    JOIN m.user u
                    JOIN m.jobTitle j
                    WHERE m.project.id = :projectId
                    AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                    AND m.generateAttendance = true
                    AND a.id IS NULL
                    AND (:jobTitle IS NULL OR j.title = :jobTitle)
                    AND (
                            :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search , '%'))
                         )
                    """
    )
    Page<ProjectMembership> findMembershipsWithoutAttendance(@Param("projectId")UUID projectId,
                                                             @Param("date")LocalDate date,
                                                             @Param("jobTitle") String jobTitle,
                                                             @Param("search") String search,
                                                             Pageable pageable);
    @Query(
            value = """
                SELECT a FROM Attendance a
                JOIN a.membership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND a.date = :date
                AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (:status IS NULL OR a.status = :status)
                AND (
                       :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """,
            countQuery = """
                SELECT COUNT(a) FROM Attendance a
                JOIN a.membership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND a.date = :date
                AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (:status IS NULL OR a.status = :status)
                AND (
                       :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """
    )
    Page<Attendance> findAttendanceRecords(@Param("projectId") UUID projectId,
                                           @Param("date") LocalDate date,
                                           @Param("jobTitle") String jobTitle,
                                           @Param("status") AttendanceStatus status,
                                           @Param("search") String search,
                                           Pageable pageable);

    @Query(
            value= """
                SELECT new net.ravik_cms.ravik_backend.attendance.AttendanceSummaryProjection(
                                m.id, u.userName, j.title, COUNT(a))
                FROM ProjectMembership m
                JOIN m.user u
                JOIN m.jobTitle j
                LEFT JOIN Attendance a
                    ON a.membership = m
                    AND a.date BETWEEN :start AND :end
                    AND a.status = net.ravik_cms.ravik_backend.common.enums.AttendanceStatus.PRESENT
                WHERE m.project.id = :projectId
                AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                     :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                    )
                GROUP BY m.id, u.userName, j.title
                """,
            countQuery = """
                    SELECT COUNT(m)
                    FROM ProjectMembership m
                    JOIN m.user u
                    JOIN m.jobTitle j
                    WHERE m.project.id = :projectId
                    AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                    AND (:jobTitle IS NULL OR j.title = :jobTitle)
                    AND (:search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')))
                """
    )
    Page<AttendanceSummaryProjection> findAttendanceForSummary(@Param("projectId") UUID projectId,
                                              @Param("start") LocalDate start,
                                              @Param("end") LocalDate end,
                                              @Param("jobTitle") String jobTitle,
                                              @Param("search") String search,
                                              Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM Attendance a
        JOIN FETCH a.membership m
        WHERE m.id IN :membershipIds
        AND a.status = net.ravik_cms.ravik_backend.common.enums.AttendanceStatus.PRESENT
        AND a.date BETWEEN :start AND :end
        AND a.wage IS NULL
    """)
    List<Attendance> findAttendanceForGeneratingWage(
            @Param("memberIds") List<Long> memberIds,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<Attendance> findByWage(Wages wage);

    @Query(
            value = """
                SELECT a FROM Attendance a
                JOIN a.membership m
                WHERE m.id = :memberId
                AND (:start IS NULL OR a.date >= :start)
                AND (:end IS NULL OR a.date <= :end)
            """,
            countQuery = """
                SELECT COUNT(a) FROM Attendance a
                JOIN a.membership m
                WHERE m.id = :memberId
                AND (:start IS NULL OR a.date >= :start)
                AND (:end IS NULL OR a.date <= :end)
            """
    )
    Page<Attendance> findAttendanceByMembership(@Param("memberId") Long memberId,
                                                @Param("start") LocalDate start,
                                                @Param("end") LocalDate end,
                                                Pageable pageable);
}
