package net.ravik_cms.ravik_backend.dailyLog;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.projects.Projects;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyLogService {
    private final DailyLogRepository dailyLogRepository;

    public DailyLog createOrFetchDailyLog (Projects project, LocalDate logDate){
        return dailyLogRepository
                .findByProjectAndLogDate(project, logDate)
                .orElseGet(()->{
                    DailyLog log = new DailyLog();
                    log.setLogDate(logDate);
                    log.setProject(project);
                    return dailyLogRepository.save(log);
                });
    }
}
