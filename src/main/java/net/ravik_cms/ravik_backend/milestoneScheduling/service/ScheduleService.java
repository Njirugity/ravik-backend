package net.ravik_cms.ravik_backend.milestoneScheduling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.*;
import net.ravik_cms.ravik_backend.milestoneScheduling.entity.MilestoneDependency;
import net.ravik_cms.ravik_backend.milestoneScheduling.repository.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MilestonesRepository milestonesRepository;
    private final ProjectsRepository projectsRepository;
    private final CalendarService calendarService;
    private final ScheduleGraph scheduleGraph;
    private final ForecastService forecastService;

    /**
     * Calculate the complete project schedule
     */
    @Transactional
    public void calculateSchedule(UUID projectId){
        //1.Get and validate the project
        Projects project =  projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        //2. Get all the project milestone for sorting
        List<Milestones> milestones =  milestonesRepository.findAllByProjectId(projectId);

        if (milestones.isEmpty()) {
            throw new ResourceNotFoundException("Project has no milestones to schedule");
        }
        Calendar calender = calendarService.getCalenderEntity(projectId);
        //Build the data required for traversal. this is the milestone with its predecessors
        //successors
        ScheduleGraph.GraphData data = scheduleGraph.build(milestones, projectId);

        //Perform the topological sort to order the milestones
        List<Milestones> sortedMilestones = scheduleGraph.topologicalSort(milestones, data);

        //Perform the forward pass to calculate the earliest dates
        forwardPass(sortedMilestones, data, project.getPlannedStart(), calender);

        //Get the project end date after performing the forward pass. We need it for backward pass
        LocalDate endDate = findEarliestProjectFinish(sortedMilestones, data);

        //Perform backward pass to calculate the latest dates
        backwardPass(sortedMilestones, data, endDate, calender);

        //Identify the critical path
        markCritical(sortedMilestones, calender);

        milestonesRepository.saveAll(sortedMilestones);
        project.setScheduled(true);
        project.setPlannedEnd(endDate);
        projectsRepository.save(project);

        //Baseline changed — the forecast (frozen actuals + re-derived CPM dates) must
        //be recomputed so it never goes stale relative to the new baseline.
        forecastService.recalculateForecastIfPossible(projectId);
    }
    /**
     * Recalculate the schedule if the project is in a schedulable state, silently
     * skipping recalculation (instead of throwing) when milestones or a calendar
     * aren't set up yet. Intended to be called after any milestone/dependency
     * mutation so the schedule never goes stale without requiring a manual
     * "Calculate Schedule" step.
     */
    @Transactional
    public void recalculateIfPossible(UUID projectId){
        try {
            calculateSchedule(projectId);
        } catch (ResourceNotFoundException | CircularDependencyException e) {
            log.warn("Schedule recalculation skipped for project {}: {}", projectId, e.getMessage());
        }
    }
    /**
     * Forward pass: Calculate the earliest start and finish dates.
     * Lag is applied per predecessor edge (via data.lagBetween) before taking the max,
     * matching ForecastService.earliestStartFromPredecessors so the baseline and
     * forecast passes can never disagree on how lag is applied.
     */
    public void forwardPass(List<Milestones>sortedMilestones,
                            ScheduleGraph.GraphData data, LocalDate startDate,
                            Calendar calendar){
        for (Milestones m : sortedMilestones){
            //1. For each milestone in sorted get its predecessor
            List<Milestones> predecessors = data.predecessorsOf(m.getId());
            //2. Mlt with no predecessors(pred) are the ones that kickoff the project
            if(predecessors.isEmpty()){
                m.setEarliestStart(startDate);
            }else{
                //3. Get the pred's earliest finish date(efd) plus the edge lag; this becomes a
                //   candidate earliest start date(esd). If a pred has no efd there is an error.
                //   For milestones with more than one pred, the largest candidate is its esd.
                LocalDate maxCandidate = null;
                for (Milestones p : predecessors){
                    LocalDate pFinish = p.getEarliestFinish();
                    if (pFinish == null) continue;
                    LocalDate candidate = calendarService.addWorkingDays(
                            pFinish, data.lagBetween(p.getId(), m.getId()), calendar);
                    if (maxCandidate == null || candidate.isAfter(maxCandidate)) {
                        maxCandidate = candidate;
                    }
                }
                if (maxCandidate == null) {
                    throw new ResourceNotFoundException(
                            "Predecessor without calculated finish date for milestone"+ m.getTitle());
                }
                m.setEarliestStart(maxCandidate);
            }
            //4. Get efd by adding the duration to esd
            LocalDate earliestFinish = calendarService.addWorkingDays(m.getEarliestStart(),
                    m.getDuration(), calendar);

            m.setEarliestFinish(earliestFinish);
        }

    }
    /**
     * Find the overall project finish date
     * This is the latest earliest finish of all milestones with no successors
     */
    private LocalDate findEarliestProjectFinish(List<Milestones> sortedMilestone,
                                           ScheduleGraph.GraphData data){
        return sortedMilestone.stream()
                .filter(m -> data.isTerminal(m.getId()))
                .map(Milestones::getEarliestFinish)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElseThrow(()-> new ResourceNotFoundException("Could not determine project finish date. No terminal milestones found."));
    }

    /**
     * Backward pass: Calculate latest start and finish dates.
     * Lag is applied per successor edge (via data.lagBetween) before taking the min,
     * mirroring ForecastService.forecastBackwardPass.
     */
    private void backwardPass(List<Milestones>sortedMilestones,
                              ScheduleGraph.GraphData data, LocalDate endDate, Calendar calendar){
        //1. Reverse sorted so that the mlt(no successors) is the first one we will go through
        List<Milestones> reversed = new ArrayList<>(sortedMilestones);
        Collections.reverse(reversed);

        for(Milestones m: reversed){
            //2. For each sorted mlt get its successors
            List<Milestones> successors = data.successorsOf(m.getId());
            //3. Mlt with no successors is the last one so it's the latest finish date(lsd) is
            //   the projects endDate
            if(successors.isEmpty()){
                m.setLatestFinish(endDate);
            }else{
                //4. Get the successors lsd minus the edge lag; this is a candidate lfd. If the
                //  successor has no lsd there is an error. If a mlt has multiple successors pick
                //  the minimum candidate — this will be the milestones lfd.
                LocalDate minCandidate = null;
                for (Milestones s : successors){
                    LocalDate sLatestStart = s.getLatestStart();
                    if (sLatestStart == null) continue;
                    LocalDate candidate = calendarService.subtractWorkingDays(
                            sLatestStart, data.lagBetween(m.getId(), s.getId()), calendar);
                    if (minCandidate == null || candidate.isBefore(minCandidate)) {
                        minCandidate = candidate;
                    }
                }
                if (minCandidate == null) {
                    throw new ResourceNotFoundException(
                            "Successor without calculated start date for milestone"+ m.getTitle());
                }
                m.setLatestFinish(minCandidate);
            }
            //5. get the lsd by subtracting duration from lfd
            LocalDate latestStart = calendarService.subtractWorkingDays(m.getLatestFinish(),
                    m.getDuration(), calendar);
            m.setLatestStart(latestStart);
        }
    }
    /**
     * Identify critical path milestones and calculate the float
     * A milestone is critical if it has zero total float
     * (earliest dates equal latest dates)
     *  Float is when the difference between earliest and latest
     * dates are not equal to zero
     */
    private void markCritical(List<Milestones> milestone, Calendar calender){

        for(Milestones m: milestone){
            boolean isCritical = m.getEarliestStart() != null &&
                    m.getLatestStart() != null &&
                    m.getEarliestStart().equals(m.getLatestStart());
            m.setCritical(isCritical);
            m.setTotalFloat(0L);

            if(!isCritical && m.getEarliestFinish() != null && m.getLatestFinish() != null){
                long totalFloat = calendarService.daysBetween(m.getEarliestStart(), m.getLatestStart(), calender);
                m.setTotalFloat(totalFloat);
            }
        }
    }
    /**
     * Reset schedule data for a project (when major changes occur)
     */
    public void resetSchedule(UUID projectId){
        Projects project =  projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);
        for(Milestones m: milestones){
            m.setEarliestStart(null);
            m.setEarliestFinish(null);
            m.setLatestStart(null);
            m.setLatestFinish(null);
            m.setCritical(false);
            m.setTotalFloat(null);
        }
        milestonesRepository.saveAll(milestones);
        project.setScheduled(false);
        project.setPlannedEnd(null);
        projectsRepository.save(project);
    }

    /**
     * Get schedule summary for a project
     */
    @Transactional(readOnly = true)
    public ScheduleSummary getScheduleSummary(UUID projectId){
        Projects project =  projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));

        if (!project.isScheduled()){
            throw new ResourceNotFoundException("Project has not been scheduled yet");
        }
        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);

        List<Milestones> criticalPath = milestones.stream()
                .filter(Milestones::isCritical)
                .sorted(Comparator.comparing(Milestones::getEarliestStart))
                .collect(Collectors.toList());
        long totalDuration = criticalPath.stream()
                .mapToInt(Milestones::getDuration)
                .sum();
        // Mapped to a narrow DTO rather than returned as entities: Milestones carries a
        // project reference, and Projects/Client reference each other back (client.projects),
        // so serialising the raw entities here recurses through that cycle indefinitely.
        List<CriticalPathMilestoneDto> criticalPathDtos = criticalPath.stream()
                .map(m -> new CriticalPathMilestoneDto(m.getId(), m.getTitle(), m.getDuration()))
                .collect(Collectors.toList());

        return new ScheduleSummary(
                project.getId(), project.getTitle(), project.getPlannedStart(), project.getPlannedEnd(), totalDuration,
                criticalPath.size(), milestones.size(), criticalPathDtos
        );
    }
    @Transactional(readOnly = true)
    public ScheduleVisualizationResponseDto getScheduleVisualization(UUID projectId){
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!project.isScheduled()) {
            throw new ResourceNotFoundException("Project has not been scheduled yet");
        }

        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);
        List<MilestoneDependency> dependencies = scheduleRepository.findByProjectId(projectId);
        Calendar calendar = calendarService.getCalenderEntity(projectId);

        // Build graph data for visualization
        ScheduleGraph.GraphData graphData = scheduleGraph.build(milestones, projectId);

        // Create AON Diagram
        AONDiagramDto aonDiagram = buildAONDiagram(milestones, dependencies, graphData);

        // Create Gantt Chart
        GanttChartDto ganttChart = buildGanttChart(milestones, dependencies, project, calendar);

        // Create Critical Path Details
        CriticalPathDetailsDto criticalPathDetails = buildCriticalPathDetails(milestones);

        // Get Schedule Summary
        ScheduleSummary scheduleSummary = getScheduleSummary(projectId);

        // Get Milestone Statuses (for project tracking)
        List<MilestoneStatusDTO> milestoneStatuses = getMilestoneStatuses(milestones);

        return new ScheduleVisualizationResponseDto(
                aonDiagram,
                ganttChart,
                criticalPathDetails,
                scheduleSummary,
                milestoneStatuses
        );
    }
    /**
     * Convert milestone to node
     */
    private NodeDto convertToNode(Milestones milestone) {
        return new NodeDto(
                milestone.getId(), milestone.getTitle(), milestone.getDuration() == 0, 0, 0
        );
    }
    /**
     * Convert MilestoneDependency to link
     */
    private LinkDto convertToLink(MilestoneDependency dependency){
        boolean isCritical = false;
        if(dependency.getPredecessor().isCritical() && dependency.getMilestone().isCritical()){
            isCritical = true;
        }
        String type = "FS";
        Integer lag = 0;

        return new LinkDto(
                UUID.randomUUID(),
                dependency.getPredecessor().getId(),
                dependency.getMilestone().getId(),
                type,
                lag,
                isCritical,
                type + (lag != null && lag > 0 ? "+" + lag : "")
        );
    }
    /**
     * Convert Milestone to GanttTaskDTO
     */
    private GanttTaskDto convertToGanttTask(Milestones milestone, List<MilestoneDependency> dependencies) {
        String color = milestone.isCritical() ? "#FF6B6B" : "#4ECDC4";

        // Calculate progress (you might want to store this in the database)
        double progress = 0.0;

        // Build dependencies
        List<GanttDependencyDto> taskDependencies = dependencies.stream()
                .filter(d -> d.getMilestone().getId().equals(milestone.getId()))
                .map(d -> new GanttDependencyDto(
                        d.getPredecessor().getId(),
                        d.getMilestone().getId(),
                        "FS", // Default
                        0 // Default lag
                ))
                .collect(Collectors.toList());

        return new GanttTaskDto(
                milestone.getId(),
                milestone.getTitle(),
                milestone.getDuration(),
                milestone.getEarliestStart(),
                milestone.getEarliestFinish(),
                milestone.isCritical(),
                milestone.getTotalFloat(),
                progress,
                taskDependencies,
                color,
                0, // rowIndex to be set later
                milestone.getDuration() == 0, // isMilestone
                milestone.getPhase() != null ? milestone.getPhase().getId() : null,
                milestone.getPhase() != null ? milestone.getPhase().getTitle() : null,
                milestone.getForecastES(),
                milestone.getForecastEF(),
                milestone.getForecastFloat(),
                milestone.isForecastCritical()
        );
    }

    private void assignNodePositions(List<NodeDto> nodes, ScheduleGraph.GraphData graphData, List<Milestones> sorted){
        Map<UUID, Integer> levels = scheduleGraph.computeLevels(sorted, graphData);
        Map<Integer, List<NodeDto>> levelMap = new HashMap<>();

        for (NodeDto node : nodes) {
            int level = levels.getOrDefault(node.getId(), 0);
            levelMap.computeIfAbsent(level, k -> new ArrayList<>()).add(node);
        }

        // Assign positions
        int xSpacing = 200;
        int ySpacing = 80;
        int xOffset = 50;

        for (Map.Entry<Integer, List<NodeDto>> entry : levelMap.entrySet()) {
            int level = entry.getKey();
            List<NodeDto> levelNodes = entry.getValue();
            int totalNodes = levelNodes.size();
            int yOffset = 50;

            for (int i = 0; i < levelNodes.size(); i++) {
                NodeDto node = levelNodes.get(i);
                node.setX(xOffset + (level * xSpacing));
                node.setY(yOffset + (i * ySpacing));
            }
        }
    }
    /**
     * Build AON Diagram data for the frontend
     */
    private AONDiagramDto buildAONDiagram(
            List<Milestones> milestones,
            List<MilestoneDependency> dependencies,
            ScheduleGraph.GraphData graphData) {

        // Sort milestones topologically (predecessors before successors) for display.
        // scheduleGraph.topologicalSort takes its own defensive copy of indegree, so
        // graphData stays intact for assignNodePositions below.
        List<Milestones> sortedMilestones = scheduleGraph.topologicalSort(milestones, graphData);

        List<NodeDto> nodes = sortedMilestones.stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        List<LinkDto> links = dependencies.stream()
                .map(this::convertToLink)
                .collect(Collectors.toList());

        // Calculate positions (simple layered layout)
        assignNodePositions(nodes, graphData, sortedMilestones);

        return new AONDiagramDto(
                nodes,
                links,
                !milestones.isEmpty() ? milestones.get(0).getProject().getId() : null,
                !milestones.isEmpty() ? milestones.get(0).getProject().getTitle() : null
        );
    }
    private GanttChartDto buildGanttChart(List<Milestones> milestones, List<MilestoneDependency> dependencies, Projects project,
                                          Calendar calendar) {
        List<GanttTaskDto> tasks = milestones.stream()
                .map(milestone -> convertToGanttTask(milestone, dependencies))
                .collect(Collectors.toList());

        // Sort tasks by start date
        tasks.sort(Comparator.comparing(GanttTaskDto::getStartDate));

        // Assign row indices
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setRowIndex(i);
        }

        // Build timeline
        LocalDate minDate = project.getPlannedStart();
        LocalDate maxDate = project.getPlannedEnd();

        // Get working days and holidays
        List<LocalDate> workingDays = new ArrayList<>();
        List<LocalDate> holidays = new ArrayList<>();

        // You might want to add methods to CalendarService to get these
        // For now, we'll keep them empty

        long totalDays = ChronoUnit.DAYS.between(minDate, maxDate);

        GanttTimelineDto timeline = new GanttTimelineDto(
                minDate,
                maxDate,
                workingDays,
                holidays,
                (int) totalDays
        );

        return new GanttChartDto(
                tasks,
                timeline,
                project.getId(),
                project.getTitle(),
                project.getPlannedStart(),
                project.getPlannedEnd(),
                (int) totalDays
        );
    }
    /**
     * Build critical path details
     */
    private CriticalPathDetailsDto buildCriticalPathDetails(List<Milestones> milestones) {
        List<UUID> criticalPathIds = milestones.stream()
                .filter(Milestones::isCritical)
                .sorted(Comparator.comparing(Milestones::getEarliestStart))
                .map(Milestones::getId)
                .collect(Collectors.toList());

        long totalDuration = milestones.stream()
                .filter(Milestones::isCritical)
                .mapToInt(Milestones::getDuration)
                .sum();

        LocalDate startDate = milestones.stream()
                .filter(Milestones::isCritical)
                .map(Milestones::getEarliestStart)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate endDate = milestones.stream()
                .filter(Milestones::isCritical)
                .map(Milestones::getLatestFinish)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        double criticalPercentage = !milestones.isEmpty() ?
                (double) criticalPathIds.size() / milestones.size() * 100 : 0;

        return new CriticalPathDetailsDto(
                criticalPathIds,
                criticalPathIds.size(),
                totalDuration,
                startDate,
                endDate,
                criticalPercentage
        );
    }
    /**
     * Get milestone statuses for project tracking
     */
    private List<MilestoneStatusDTO> getMilestoneStatuses(List<Milestones> milestones) {
        return milestones.stream()
                .map(m->{
                    return convertToMilestoneStatus(m);
                })
                .collect(Collectors.toList());
    }

    /**
     * Convert Milestone to MilestoneStatusDTO
     */
    private MilestoneStatusDTO convertToMilestoneStatus(Milestones milestone) {
        ProgressStatus status = milestone.getStatus();

        LocalDate actualStart = milestone.getActualStartDate();
        LocalDate actualFinish = milestone.getActualEndDate();

        Double completionPercentage = 0.0;

        boolean isDelayed = false;
        long delayDays = 0;

        // Check if delayed (actual > planned)
        if (actualFinish != null && milestone.getLatestFinish() != null) {
            if (actualFinish.isAfter(milestone.getLatestFinish())) {
                isDelayed = true;
                delayDays = ChronoUnit.DAYS.between(milestone.getLatestFinish(), actualFinish);
            }
        }

        String color = milestone.isCritical() ? "#FF6B6B" : "#4ECDC4";

        return new MilestoneStatusDTO(
                milestone.getId(),
                milestone.getTitle(),
                status,
                actualStart,
                actualFinish,
                completionPercentage,
                isDelayed,
                delayDays,
                color
        );
    }
}
