package net.ravik_cms.ravik_backend.dailyLog;

import net.ravik_cms.ravik_backend.projects.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findByProjectAndLogDate(Projects project, LocalDate logDate);
}
