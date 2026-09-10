package net.ravik_cms.ravik_backend.milestones.service;

import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateEvaluator {
    public DateStatus calculateDateStatus(Milestones m){
        LocalDate today = LocalDate.now();
        // 1. Milestone is fully completed
        if (m.getActualEndDate() != null) {
            LocalDate latestFinish = m.getLatestFinish();
            if (latestFinish == null) {
                return DateStatus.NOT_SCHEDULED;
            }
            return m.getActualEndDate().isAfter(latestFinish)
                    ? DateStatus.COMPLETED_LATE
                    : DateStatus.COMPLETED_ON_TIME;
        }

        // 2. Milestone is currently active (In Progress)
        if (m.getActualStartDate() != null) {
            LocalDate latestFinish = m.getLatestFinish();
            if (latestFinish == null) {
                return DateStatus.NOT_SCHEDULED;
            }
            return today.isAfter(latestFinish)
                    ? DateStatus.OVERDUE
                    : DateStatus.ONTRACK;
        }

        // 3. Milestone has NOT started yet (Actual Start is null)
        LocalDate latestStart = m.getLatestStart();
        LocalDate latestFinish = m.getLatestFinish();
        LocalDate earliestStart = m.getEarliestStart();

        if (latestStart != null && today.isAfter(latestStart)) {
            return DateStatus.DELAYED_START;
        }

        if (latestFinish != null && today.isAfter(latestFinish)) {
            return DateStatus.OVERDUE;
        }

        if (earliestStart != null) {
            return today.isBefore(earliestStart)
                    ? DateStatus.NOT_STARTED
                    : DateStatus.ONTRACK;
        }

        // Fallback if all schedule dates are null (scheduler hasn't been run)
        return DateStatus.NOT_SCHEDULED;
    }
}
