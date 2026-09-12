package net.ravik_cms.ravik_backend.milestoneScheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.calendar.Calendar;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
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
        GraphData data = buildGraphData(milestones, projectId);

        //Perform the topological sort to order the milestones
        List<Milestones> sortedMilestones = topologicalSort(milestones,
                data.indegree, data.successorMap);

        //Perform the forward pass to calculate the earliest dates
        forwardPass(sortedMilestones, data.predecessorMap, project.getPlannedStart(), calender);

        //Get the project end date after performing the forward pass. We need it for backward pass
        LocalDate endDate = findEarliestProjectFinish(sortedMilestones, data.successorMap);

        //Perform backward pass to calculate the latest dates
        backwardPass(sortedMilestones, data.successorMap, endDate, calender);

        //Identify the critical path
        markCritical(sortedMilestones);

        milestonesRepository.saveAll(sortedMilestones);
        project.setScheduled(true);
        project.setPlannedEnd(endDate);
        projectsRepository.save(project);
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
     * Internal data class to hold graph structures
     */
    private static class GraphData{
        final Map<UUID, List<Milestones>> successorMap;
        final Map<UUID, List<Milestones>> predecessorMap;
        final Map<UUID, Integer> indegree;

        public GraphData(Map<UUID, List<Milestones>> successorMap, Map<UUID,
                List<Milestones>> predecessorMap, Map<UUID, Integer> indegree) {
            this.successorMap = successorMap;
            this.predecessorMap = predecessorMap;
            this.indegree = indegree;
        }
    }
    /**
     * Build all the maps needed for graph traversal
     */
    private GraphData buildGraphData(List<Milestones> milestones, UUID projectId){
        Map<UUID, List<Milestones>> successorMap = new HashMap<>();
        Map<UUID, List<Milestones>> predecessorMap =  new HashMap<>();
        Map<UUID, Integer> indegree =  new HashMap<>();

        //Populate the maps with each milestones id and an empty array that we will
        // populate with the milestone predecessor and successors later
        for (Milestones m: milestones){
            successorMap.put(m.getId(), new ArrayList<>());
            predecessorMap.put(m.getId(), new ArrayList<>());
            indegree.put(m.getId(), 0);
        }

        //Get all the records from the milestoneDependency table
        List<MilestoneDependency> allDependencies = scheduleRepository.findByProjectId(projectId);

        //Populate the maps empty arrays with their correct successors and predecessors
        for (MilestoneDependency d: allDependencies){
            UUID milestoneId = d.getMilestone().getId();
            UUID predecessorId = d.getPredecessor().getId();

            //Predecessor has this d as its successor
            successorMap.get(predecessorId).add(d.getMilestone());
            //Milestone has d as its predecessor
            predecessorMap.get(milestoneId).add(d.getPredecessor());
            //Indegree is the number of predecessors a milestone has.
            indegree.merge(milestoneId, 1, Integer::sum);
        }
        return new GraphData(successorMap, predecessorMap, indegree);
    }
    /**
     * Kahn's Algorithm for topological sorting
     * Orders milestones so each appears after all its dependencies
     * Visit <a href="https://youtu.be/cIBFEhD77b4?si=z_-FhbRpvPyRffPl">...</a> to get and idea of how it works
     */
    private List<Milestones> topologicalSort(
            List<Milestones> milestones, Map<UUID, Integer> indegree,
            Map<UUID, List<Milestones>> successorMap){
        //1.Create a list to store the sorted milestones
        List<Milestones> sorted = new ArrayList<>();

        //2.Create lookup for quick milestone access by id
        Map<UUID, Milestones> milestonesMap = milestones.stream()
              .collect(Collectors.toMap(Milestones::getId, milestone -> milestone));

        //2.Create a Queue for nodes with no incoming edges. Queue is used for its efficiency in
        //  tracking and processing and FIFO application
        Queue<Milestones> queue = new LinkedList<>();

        //3.Find all milestones with inDegree = 0 (no predecessors) and add them to the queue
        for(Milestones m: milestones){
            if(indegree.get(m.getId())== 0){
                queue.offer(m);
            }
        }
        //-Topological sorts are purely acyclic so you need to avoid cycle or get to an infinite loop
        if(queue.isEmpty()){
            throw new CircularDependencyException(
                    "No start milestone found. Every milestone has a predecessor, possible cycle.");
        }
        //4. Populate the sorted array
        while(!queue.isEmpty()){
            //4.1.the queue contains milestones(mlt) with no predecessors. So remove it from the queue
            //    and add it to the sorted array
            Milestones current = queue.poll();
            sorted.add(current);
            //4.2.For the removed mlt we need to find the mlt that go after it, thus we need its successor
            for (Milestones successor : successorMap.get(current.getId())) {
                //4.2.1.Reduce the indegree of the successor
                int newIndegree = indegree.merge(successor.getId(), -1, Integer::sum);
                //4.2.2.If its zero add it to the queue, if not skip the step and go back to the start
                //      of the while loop.
                if(newIndegree == 0){
                    queue.offer(milestonesMap.get(successor.getId()));
                }
            }
        }
        //5. Confirm that all the milestones are sorted. sorted should be equal to milestone
        if(sorted.size() != milestones.size()){
            //5.1. Get all the id for easy look-ups
            Set<UUID> sortedIds = sorted.stream()
                    .map(Milestones::getId)
                    .collect(Collectors.toSet());
            //5.2 filter milestone that are not in sorted and throw an error
            List<String> unsorted = milestones.stream()
                    .filter(m->!sortedIds.contains(m.getId()))
                    .map(Milestones::getTitle)
                    .toList();
            throw new CircularDependencyException(
                    "Circular dependency detected involving milestones: " + unsorted);
        }
        return sorted;
    }
    /**
     * Forward pass: Calculate the earliest start and finish dates
     */
    public void forwardPass(List<Milestones>sortedMilestones,
                            Map<UUID, List<Milestones>>predecessorsMap, LocalDate startDate,
                            Calendar calendar){
        for (Milestones m : sortedMilestones){
            //1. For each milestone in sorted get its predecessor
            List<Milestones> predecessors = predecessorsMap.getOrDefault(
                    m.getId(), Collections.emptyList()
            );
            //2. Mlt with no predecessors(pred) are the ones that kickoff the project
            if(predecessors.isEmpty()){
                m.setEarliestStart(startDate);
            }else{
                //3. Get the pred's earliest finish date(efd), this automatically becomes the milestones
                //   earliest start date(esd). If a pred has no efd there is an error. For milestones with
                //   more than one preds the largest one is its esd.
                LocalDate maxPredecessorFinishDate = predecessors.stream()
                        .map(Milestones::getEarliestFinish)
                        .filter(Objects::nonNull)
                        .max(LocalDate::compareTo)
                        .orElseThrow(()-> new ResourceNotFoundException(
                                "Predecessor without calculated finish date for milestone"+ m.getTitle()
                        ));

                m.setEarliestStart(calendarService.addWorkingDays(maxPredecessorFinishDate, 2, calendar));
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
                                           Map<UUID, List<Milestones>>successorMap){
        return sortedMilestone.stream()
                .filter(m->{
                    List<Milestones> successors = successorMap.get(m.getId());
                    return successors == null || successors.isEmpty();
                })
                .map(Milestones::getEarliestFinish)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElseThrow(()-> new ResourceNotFoundException("Could not determine project finish date. No terminal milestones found."));
    }

    /**
     * Backward pass: Calculate latest start and finish dates
     */
    private void backwardPass(List<Milestones>sortedMilestones,
                              Map<UUID, List<Milestones>>successorMap, LocalDate endDate, Calendar calendar){
        //1. Reverse sorted so that the mlt(no successors) is the first one we will go through
        List<Milestones> reversed = new ArrayList<>(sortedMilestones);
        Collections.reverse(reversed);

        for(Milestones m: reversed){
            //2. For each sorted mlt get its successors
            List<Milestones> successors = successorMap.getOrDefault(
                    m.getId(), Collections.emptyList()
            );
            //3. Mlt with no successors is the last one so it's the latest finish date(lsd) is
            //   the projects endDate
            if(successors.isEmpty()){
                m.setLatestFinish(endDate);
            }else{
                //4. Get the successors lsd this will be the milestones lfd. If the successor
                //  has no lsd there is an error. If a mlt has multiple successors pick the
                //  the minimum date this will be the milestones lsd
                LocalDate minSuccessorStartDate = successors.stream()
                        .map(Milestones::getLatestStart)
                        .filter(Objects::nonNull)
                        .min(LocalDate::compareTo)
                        .orElseThrow(()-> new ResourceNotFoundException(
                                "Successor without calculated start date for milestone"+ m.getTitle()
                        ));
                m.setLatestFinish(calendarService.subtractWorkingDays(minSuccessorStartDate, 2, calendar));
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
    private void markCritical(List<Milestones> milestone){

        for(Milestones m: milestone){
            boolean isCritical = m.getEarliestStart() != null &&
                    m.getLatestStart() != null &&
                    m.getEarliestStart().equals(m.getLatestStart())&&
                    m.getLatestFinish().equals(m.getLatestFinish());
            m.setCritical(isCritical);

            if(!isCritical && m.getEarliestFinish() != null && m.getLatestFinish() != null){
                long totalFloat = ChronoUnit.DAYS.between(m.getEarliestStart(), m.getLatestStart());
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

        return new ScheduleSummary(
                project.getId(), project.getTitle(), project.getPlannedStart(), project.getPlannedEnd(), totalDuration,
                criticalPath.size(), milestones.size(), criticalPath
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
        GraphData graphData = buildGraphData(milestones, projectId);

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
        ProgressStatus status = milestone.getStatus();
        String color = milestone.isCritical() ? "#FF6B6B" : "#4ECDC4";
        return new NodeDto(
                milestone.getId(), milestone.getTitle(), milestone.getDuration(), milestone.getEarliestStart(),
                milestone.getEarliestFinish(), milestone.getLatestStart(), milestone.getLatestFinish(),
                milestone.getTotalFloat(), milestone.isCritical(), milestone.getDuration()==0,
                status, 0, 0, color
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
                milestone.getPhase().getId(),
                milestone.getPhase().getTitle()
        );
    }

    private void assignNodePositions(List<NodeDto> nodes, GraphData graphData){
        Map<Integer, List<NodeDto>> levelMap = new HashMap<>();

        for (NodeDto node : nodes) {
            int level = calculateNodeLevel(node.getId(), graphData);
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
     * Calculate a nodes positioning
     */
    private int calculateNodeLevel(UUID nodeId, GraphData graphData){
        Map<UUID, Integer> levels = new HashMap<>();
        Queue<UUID> queue = new LinkedList<>();
        for (Map.Entry<UUID, Integer> entry : graphData.indegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.offer(entry.getKey());
                levels.put(entry.getKey(), 0);
            }
        }
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            int currentLevel = levels.get(current);

            for (Milestones successor : graphData.successorMap.getOrDefault(current, Collections.emptyList())) {
                UUID successorId = successor.getId();
                if (!levels.containsKey(successorId) || levels.get(successorId) < currentLevel + 1) {
                    levels.put(successorId, currentLevel + 1);
                    queue.offer(successorId);
                }
            }
        }

        return levels.getOrDefault(nodeId, 0);
    }
    /**
     * Build AON Diagram data for the frontend
     */
    private AONDiagramDto buildAONDiagram(
            List<Milestones> milestones,
            List<MilestoneDependency> dependencies,
            GraphData graphData) {

        List<NodeDto> nodes = milestones.stream()
                .map(this::convertToNode)
                .collect(Collectors.toList());

        List<LinkDto> links = dependencies.stream()
                .map(this::convertToLink)
                .collect(Collectors.toList());

        // Calculate positions (simple layered layout)
        assignNodePositions(nodes, graphData);

        // Find project start and end
        LocalDate projectStart = milestones.stream()
                .map(Milestones::getEarliestStart)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        LocalDate projectEnd = milestones.stream()
                .map(Milestones::getLatestFinish)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        return new AONDiagramDto(
                nodes,
                links,
                !milestones.isEmpty() ? milestones.get(0).getProject().getId() : null,
                !milestones.isEmpty() ? milestones.get(0).getProject().getTitle() : null,
                projectStart,
                projectEnd
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
                .map(this::convertToMilestoneStatus)
                .collect(Collectors.toList());
    }

    /**
     * Convert Milestone to MilestoneStatusDTO
     */
    private MilestoneStatusDTO convertToMilestoneStatus(Milestones milestone) {
        ProgressStatus status = milestone.getStatus();
        // You might want to store actual dates in the database
        // For now, we'll use estimated dates as actual dates
        LocalDate actualStart = milestone.getEarliestStart();
        LocalDate actualFinish = milestone.getEarliestFinish();
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
