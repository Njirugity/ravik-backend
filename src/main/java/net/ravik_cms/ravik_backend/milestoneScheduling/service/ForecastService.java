package net.ravik_cms.ravik_backend.milestoneScheduling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/**
 * Re-forecasts a project schedule from recorded actuals.
 *
 * ── The separation that makes this work ──────────────────────────────────
 *
 *  BASELINE  (existing fields: earliestStart/Finish, latestStart/Finish,
 *             totalFloat, critical)
 *      Written ONLY by ScheduleService.calculateSchedule().
 *      This is what the Gantt draws as the thin reference bar.
 *
 *  FORECAST  (fields: forecastES/EF, forecastLS/LF, forecastFloat, forecastCritical)
 *      Rewritten in full by this class on every run.
 *      Seeded with actuals where they exist, planned durations where they don't.
 *      This is what the Gantt draws as the solid bar, and what drives the
 *      "this milestone is pushing the project N days late" signal.
 *
 *  Variance is then just (forecast − baseline).
 * ─────────────────────────────────────────────────────────────────────────
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastService {

    private final MilestonesRepository milestonesRepository;
    private final ProjectsRepository projectsRepository;
    private final CalendarService calendarService;
    private final ScheduleGraph scheduleGraph;

    /**
     * Unstarted work cannot begin in the past. When true, any milestone with no
     * actual start is pushed forward to the data date, so an ignored milestone
     * that "should have started last week" surfaces as slippage instead of
     * quietly retaining a stale past date.
     */
    private static final boolean PUSH_UNSTARTED_TO_DATA_DATE = true;

    /** Float at or below this (working days) is flagged AT_RISK in the UI. */
    private static final long NEAR_CRITICAL_THRESHOLD = 3;

    // =====================================================================
    //  ENTRY POINT
    // =====================================================================

    /**
     * Full re-forecast. Cheap (O(V+E)) — never try to do this incrementally.
     * Partial invalidation on a DAG is a bug farm; just rerun the whole thing.
     */
    @Transactional
    public ForecastResult recalculateForecast(UUID projectId) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.isScheduled()) {
            throw new ResourceNotFoundException("Project has not been scheduled yet");
        }

        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);
        if (milestones.isEmpty()) {
            throw new ResourceNotFoundException("Project has no milestones to forecast");
        }

        Calendar calendar = calendarService.getCalenderEntity(projectId);
        LocalDate dataDate = resolveDataDate(project);

        ScheduleGraph.GraphData data = scheduleGraph.build(milestones, projectId);
        List<Milestones> sorted = scheduleGraph.topologicalSort(milestones, data);

        List<LogicViolation> violations =
                forecastForwardPass(sorted, data, project.getPlannedStart(), dataDate, calendar);

        LocalDate forecastFinish = findForecastFinish(sorted, data);

        // Anchor choice: if a contractual finish exists, anchor there so float can
        // go negative and read as "days late". Otherwise anchor on the forecast
        // itself, which makes terminal float zero by construction.
        LocalDate backwardAnchor = resolveBackwardAnchor(project, forecastFinish);

        forecastBackwardPass(sorted, data, backwardAnchor, calendar);
        markForecastCritical(sorted, calendar);

        milestonesRepository.saveAll(sorted);

        project.setForecastEnd(forecastFinish);
        project.setDataDate(dataDate);
        projectsRepository.save(project);

        LocalDate baselineFinish = project.getPlannedEnd();
        long projectVariance = baselineFinish == null ? 0
                : calendarService.workingDaysBetween(baselineFinish, forecastFinish, calendar);

        log.info("Forecast for project {}: finish={} baseline={} variance={}d violations={}",
                projectId, forecastFinish, baselineFinish, projectVariance, violations.size());

        return new ForecastResult(forecastFinish, baselineFinish, projectVariance,
                buildVarianceRows(sorted, calendar), violations);
    }

    /** Non-throwing variant for wiring into mutation hooks. */
    @Transactional
    public void recalculateForecastIfPossible(UUID projectId) {
        try {
            recalculateForecast(projectId);
        } catch (ResourceNotFoundException | CircularDependencyException e) {
            log.warn("Forecast recalculation skipped for project {}: {}", projectId, e.getMessage());
        }
    }

    // =====================================================================
    //  FORWARD PASS — reality overrides logic
    // =====================================================================

    /**
     * Three cases per milestone:
     *
     *   COMPLETE     (actualStartDate + actualEndDate)
     *       Dates are FROZEN to the actuals. We still CHECK the network logic and
     *       report a violation if it started before a predecessor finished.
     *
     *   IN PROGRESS  (actualStartDate, no actualEndDate)
     *       Start is frozen to the actual. Finish = data date + remaining
     *       duration, because remaining work starts now, not at the original
     *       planned start.
     *
     *   NOT STARTED  (no actuals)
     *       Normal CPM: start = max(predecessor finishes + lag), finish =
     *       start + duration. Clamped forward to the data date so unstarted
     *       work is never forecast to begin in the past.
     */
    private List<LogicViolation> forecastForwardPass(List<Milestones> sorted,
                                                     ScheduleGraph.GraphData data,
                                                     LocalDate projectStart,
                                                     LocalDate dataDate,
                                                     Calendar calendar) {
        List<LogicViolation> violations = new ArrayList<>();

        for (Milestones m : sorted) {
            LocalDate earliestByLogic = earliestStartFromPredecessors(m, data, projectStart, calendar);

            LocalDate fStart;
            LocalDate fFinish;

            if (m.getActualEndDate() != null) {
                // ---- COMPLETE ----
                fStart = m.getActualStartDate() != null ? m.getActualStartDate() : earliestByLogic;
                fFinish = m.getActualEndDate();

                if (earliestByLogic != null && fStart.isBefore(earliestByLogic)) {
                    violations.add(new LogicViolation(m.getId(), m.getTitle(),
                            "Started " + calendarService.workingDaysBetween(fStart, earliestByLogic, calendar)
                                    + " working day(s) before its predecessors were forecast to finish"));
                }

            } else if (m.getActualStartDate() != null) {
                // ---- IN PROGRESS ----
                fStart = m.getActualStartDate();
                int remaining = resolveRemainingDuration(m, dataDate, calendar);
                LocalDate resumeFrom = maxDate(dataDate, fStart);
                fFinish = calendarService.addWorkingDays(resumeFrom, remaining, calendar);

                if (earliestByLogic != null && fStart.isBefore(earliestByLogic)) {
                    violations.add(new LogicViolation(m.getId(), m.getTitle(),
                            "In progress but started ahead of its predecessor logic"));
                }

            } else {
                // ---- NOT STARTED ----
                fStart = earliestByLogic != null ? earliestByLogic : projectStart;
                if (PUSH_UNSTARTED_TO_DATA_DATE) {
                    fStart = maxDate(fStart, dataDate);
                }
                fFinish = calendarService.addWorkingDays(fStart, m.getDuration(), calendar);
            }

            m.setForecastES(fStart);
            m.setForecastEF(fFinish);
        }

        return violations;
    }

    private LocalDate earliestStartFromPredecessors(Milestones m,
                                                    ScheduleGraph.GraphData data,
                                                    LocalDate projectStart,
                                                    Calendar calendar) {
        List<Milestones> predecessors = data.predecessorsOf(m.getId());
        if (predecessors.isEmpty()) {
            return projectStart;
        }

        LocalDate latest = null;
        for (Milestones p : predecessors) {
            // Read the FORECAST finish — the topological order guarantees every
            // predecessor has already been processed in this same pass.
            LocalDate pFinish = p.getForecastEF();
            if (pFinish == null) continue;

            LocalDate candidate = calendarService.addWorkingDays(
                    pFinish, data.lagBetween(p.getId(), m.getId()), calendar);

            if (latest == null || candidate.isAfter(latest)) {
                latest = candidate;
            }
        }
        return latest;
    }

    /**
     * Remaining duration for in-progress work.
     *
     * Prefer an explicit re-estimate from the site team (`remainingDuration` on
     * the entity) — that lets a foreman say "this is going to take four more
     * days" and have the whole downstream network respond.
     *
     * Fall back to elapsed-based decay so the forecast is still sane when nobody
     * has re-estimated.
     */
    private int resolveRemainingDuration(Milestones m, LocalDate dataDate, Calendar calendar) {
        if (m.getRemainingDuration() != null) {
            return Math.max(0, m.getRemainingDuration());
        }
        long elapsed = calendarService.workingDaysBetween(m.getActualStartDate(), dataDate, calendar);
        return (int) Math.max(0, m.getDuration() - elapsed);
    }

    // =====================================================================
    //  PROJECT FINISH + BACKWARD PASS
    // =====================================================================

    private LocalDate findForecastFinish(List<Milestones> sorted, ScheduleGraph.GraphData data) {
        return sorted.stream()
                .filter(m -> data.isTerminal(m.getId()))
                .map(Milestones::getForecastEF)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Could not determine forecast finish. No terminal milestones found."));
    }

    /**
     * Anchoring the backward pass.
     *
     *  - Contract date present  → float measures distance from the CONTRACTUAL
     *    deadline. Float goes negative when forecasting a miss, which is the
     *    number that actually matters commercially.
     *
     *  - No contract date       → anchor on the forecast finish, so the critical
     *    path always exists at float 0 and project impact is read off
     *    ForecastResult.projectVarianceDays instead.
     */
    private LocalDate resolveBackwardAnchor(Projects project, LocalDate forecastFinish) {
        LocalDate contract = project.getContractFinish();
        return contract != null ? contract : forecastFinish;
    }

    /**
     * Backward pass is NOT frozen for completed milestones — LS/LF depend only
     * on successors, so they compute identically regardless of completion state.
     *
     * Float on a finished milestone is then a diagnostic rather than an
     * actionable number: "we had 4 days of buffer and burned 6" is exactly the
     * post-mortem signal you want, and it is how secretly-critical milestones
     * get found.
     */
    private void forecastBackwardPass(List<Milestones> sorted,
                                      ScheduleGraph.GraphData data,
                                      LocalDate anchor,
                                      Calendar calendar) {
        List<Milestones> reversed = new ArrayList<>(sorted);
        Collections.reverse(reversed);

        for (Milestones m : reversed) {
            List<Milestones> successors = data.successorsOf(m.getId());

            LocalDate lFinish;
            if (successors.isEmpty()) {
                lFinish = anchor;
            } else {
                LocalDate earliest = null;
                for (Milestones s : successors) {
                    LocalDate sLatestStart = s.getForecastLS();
                    if (sLatestStart == null) continue;

                    LocalDate candidate = calendarService.subtractWorkingDays(
                            sLatestStart, data.lagBetween(m.getId(), s.getId()), calendar);

                    if (earliest == null || candidate.isBefore(earliest)) {
                        earliest = candidate;
                    }
                }
                lFinish = earliest != null ? earliest : anchor;
            }

            m.setForecastLF(lFinish);
            m.setForecastLS(calendarService.subtractWorkingDays(lFinish, m.getDuration(), calendar));
        }
    }

    /**
     * Float + criticality on the FORECAST network.
     *
     * The critical path is not static. When a milestone slips, downstream float
     * shrinks; when it hits zero that chain BECOMES critical. When something
     * finishes early it hands slack back and the critical path can jump to a
     * completely different chain. Recomputing this on every actuals update is
     * the whole point — it tells a PM which milestones to manage THIS week
     * rather than which ones mattered at kickoff.
     */
    private void markForecastCritical(List<Milestones> milestones, Calendar calendar) {
        for (Milestones m : milestones) {
            if (m.getForecastES() == null || m.getForecastLS() == null) {
                m.setForecastFloat(null);
                m.setForecastCritical(false);
                continue;
            }

            long totalFloat = calendarService.workingDaysBetween(
                    m.getForecastES(), m.getForecastLS(), calendar);

            // ALWAYS set float, including for critical milestones, so a milestone
            // that just became critical doesn't silently keep a stale prior value.
            m.setForecastFloat(totalFloat);
            m.setForecastCritical(totalFloat <= 0);
        }
    }

    // =====================================================================
    //  VARIANCE REPORTING — the "impact" the Gantt is missing
    // =====================================================================

    private List<MilestoneVariance> buildVarianceRows(List<Milestones> milestones, Calendar calendar) {
        List<MilestoneVariance> rows = new ArrayList<>();

        for (Milestones m : milestones) {
            LocalDate baselineFinish = m.getEarliestFinish();           // frozen baseline
            LocalDate effectiveFinish = m.getActualEndDate() != null
                    ? m.getActualEndDate()
                    : m.getForecastEF();

            Long finishVariance = (baselineFinish == null || effectiveFinish == null)
                    ? null
                    : calendarService.workingDaysBetween(baselineFinish, effectiveFinish, calendar);

            Long floatErosion = (m.getTotalFloat() == null || m.getForecastFloat() == null)
                    ? null
                    : m.getTotalFloat() - m.getForecastFloat();

            rows.add(new MilestoneVariance(
                    m.getId(),
                    m.getTitle(),
                    baselineFinish,
                    m.getForecastEF(),
                    m.getActualStartDate(),
                    m.getActualEndDate(),
                    finishVariance,
                    m.getForecastFloat(),
                    floatErosion,
                    m.isForecastCritical(),
                    classify(finishVariance, m.getForecastFloat()),
                    // A milestone DRIVES project delay only if it is both late and
                    // has no float left to absorb it. Late-but-floating is noise.
                    finishVariance != null && finishVariance > 0
                            && m.getForecastFloat() != null && m.getForecastFloat() <= 0
            ));
        }
        return rows;
    }

    private VarianceStatus classify(Long finishVariance, Long forecastFloat) {
        if (finishVariance == null) return VarianceStatus.UNKNOWN;
        if (finishVariance < 0) return VarianceStatus.AHEAD;
        if (finishVariance > 0 && (forecastFloat == null || forecastFloat <= 0)) {
            return VarianceStatus.DELAYED;
        }
        if (finishVariance > 0) return VarianceStatus.ABSORBED;
        if (forecastFloat != null && forecastFloat <= NEAR_CRITICAL_THRESHOLD) {
            return VarianceStatus.AT_RISK;
        }
        return VarianceStatus.ON_TRACK;
    }

    public enum VarianceStatus {
        AHEAD,      // finishing before baseline
        ON_TRACK,   // on baseline, comfortable float
        AT_RISK,    // on baseline but float nearly exhausted
        ABSORBED,   // late, but float soaked it up — no project impact
        DELAYED,    // late AND out of float — pushing the project finish
        UNKNOWN
    }

    // =====================================================================
    //  HELPERS
    // =====================================================================

    private LocalDate resolveDataDate(Projects project) {
        return project.getDataDate() != null ? project.getDataDate() : LocalDate.now();
    }

    private LocalDate maxDate(LocalDate a, LocalDate b) {
        if (a == null) return b;
        if (b == null) return a;
        return a.isAfter(b) ? a : b;
    }

    // =====================================================================
    //  RESULT TYPES
    // =====================================================================

    public record ForecastResult(
            LocalDate forecastFinish,
            LocalDate baselineFinish,
            long projectVarianceDays,          // + = late, − = ahead
            List<MilestoneVariance> milestones,
            List<LogicViolation> violations
    ) {}

    public record MilestoneVariance(
            UUID milestoneId,
            String title,
            LocalDate baselineFinish,
            LocalDate forecastFinish,
            LocalDate actualStart,
            LocalDate actualFinish,
            Long finishVarianceDays,
            Long forecastFloat,
            Long floatErosionDays,             // baselineFloat − forecastFloat
            boolean forecastCritical,
            VarianceStatus status,
            boolean drivingProjectDelay
    ) {}

    public record LogicViolation(UUID milestoneId, String title, String message) {}
}
