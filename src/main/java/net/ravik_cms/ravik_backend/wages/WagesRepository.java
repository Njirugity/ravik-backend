package net.ravik_cms.ravik_backend.wages;

import net.ravik_cms.ravik_backend.memberships.ProjectMembership;
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
public interface WagesRepository extends JpaRepository<Wages, Long> {
    boolean existsByMembershipAndStartDateAndEndDate(ProjectMembership membership, LocalDate start, LocalDate end);
    List<Wages> findAllByMembershipInAndStartDateAndEndDate(List<ProjectMembership> membership, LocalDate start, LocalDate end);

    @Query(
            value = """
            SELECT new net.ravik_cms.ravik_backend.wages.WageCalculationDataProjection(
                        m.id, u.userName, j.title, m.baseWage, COUNT(a), m.frequency
                        )
            FROM ProjectMembership m
            JOIN m.user u
            JOIN m.jobTitle j
            LEFT JOIN Attendance a
                ON a.membership = m
                AND a.date BETWEEN :start AND :end
                AND a.status = net.ravik_cms.ravik_backend.common.enums.AttendanceStatus.PRESENT
                AND a.wage IS NULL
            WHERE m.project.id = :projectId
            AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
            AND m.frequency = net.ravik_cms.ravik_backend.common.enums.PaymentFrequency.DAILY
            AND (:jobTitle IS NULL OR j.title = :jobTitle)
            AND (
                 :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                 )
            GROUP BY m.id, u.userName, j.title, m.baseWage, m.frequency
            """,
            countQuery = """
                    SELECT COUNT(m)
                    FROM ProjectMembership m
                    JOIN m.user u
                    JOIN m.jobTitle j
                    WHERE m.project.id = :projectId
                    AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                    AND m.frequency = net.ravik_cms.ravik_backend.common.enums.PaymentFrequency.DAILY
                    AND (:jobTitle IS NULL OR j.title = :jobTitle)
                    AND (:search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')))
                    """
    )
    Page<WageCalculationDataProjection> findWageDataForDailyFrequency(
            @Param("projectId") UUID projectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("jobTitle") String jobTitle,
            @Param("search") String search,
            Pageable pageable
    );
    @Query(
            value = """
                SELECT m FROM ProjectMembership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                AND m.frequency = net.ravik_cms.ravik_backend.common.enums.PaymentFrequency.MONTHLY
                AND NOT EXISTS(
                        SELECT 1 FROM Wages w
                        WHERE w.membership = m
                        AND w.startDate = :start
                        AND w.endDate = :end
                    )
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                     :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """,
            countQuery = """
                SELECT COUNT(m) FROM ProjectMembership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND m.status = net.ravik_cms.ravik_backend.common.enums.StaffStatus.ACTIVE
                AND m.frequency = net.ravik_cms.ravik_backend.common.enums.PaymentFrequency.MONTHLY
                AND NOT EXISTS(
                        SELECT 1 FROM Wages w
                        WHERE w.membership = m
                        AND w.startDate = :start
                        AND w.endDate = :end
                    )
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                     :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
                """
    )
    Page<ProjectMembership> findWageDataForMonthlyFrequency(
            @Param("projectId") UUID projectId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("jobTitle") String jobTitle,
            @Param("search") String search,
            Pageable pageable
    );

    @Query(
            value = """
                SELECT new net.ravik_cms.ravik_backend.wages.WageHistoryProjection(
                    w.id, m.id, u.userName, j.title, w.startDate, w.endDate, w.grossPay,
                    m.frequency, w.createdAt)
                FROM Wages w
                JOIN w.membership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND (:memberId IS NULL OR m.id = :memberId)
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                     :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
            """,
            countQuery = """
                SELECT COUNT(w) FROM Wages w
                JOIN w.membership m
                JOIN m.user u
                JOIN m.jobTitle j
                WHERE m.project.id = :projectId
                AND (:memberId IS NULL OR m.id = :memberId)
                AND (:jobTitle IS NULL OR j.title = :jobTitle)
                AND (
                     :search IS NULL OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%'))
                     )
                """
    )
    Page<WageHistoryProjection> findWageHistory(
            @Param("projectId") UUID projectId,
            @Param("memberId") Long memberId,
            @Param("jobTitle") String jobTitle,
            @Param("search") String search,
            Pageable pageable
    );
}
