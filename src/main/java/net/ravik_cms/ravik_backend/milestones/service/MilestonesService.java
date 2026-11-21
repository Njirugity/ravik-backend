package net.ravik_cms.ravik_backend.milestones.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.CreateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.entity.MilestoneBudget;
import net.ravik_cms.ravik_backend.milestoneBudget.mapper.MilestoneBudgetMapper;
import net.ravik_cms.ravik_backend.milestoneBudget.repository.MilestoneBudgetRepository;
import net.ravik_cms.ravik_backend.milestoneScheduling.repository.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.ForecastService;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.ScheduleService;
import net.ravik_cms.ravik_backend.milestones.dtos.CreateMilestoneDto;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneInfoDto;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneSummaryDto;
import net.ravik_cms.ravik_backend.milestones.dtos.PossibleActiveMilestonesDto;
import net.ravik_cms.ravik_backend.milestones.dtos.UpdateMilestoneDto;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.mapper.MilestonesMapper;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.phase.repository.PhasesRepository;
import net.ravik_cms.ravik_backend.phase.service.PhasesService;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.ravik_cms.ravik_backend.milestoneScheduling.entity.MilestoneDependency;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MilestonesService {
    private final MilestonesRepository milestonesRepository;
    private final PhasesRepository phasesRepository;
    private final MilestonesMapper milestonesMapper;
    private final ProjectsRepository projectsRepository;
    private final PhasesService phasesService;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleService scheduleService;
    private final ForecastService forecastService;
    private final MilestoneBudgetRepository milestoneBudgetRepository;
    private final MilestoneBudgetMapper milestoneBudgetMapper;
//    @Scheduled(cron = "0 0 0 * * *")
//    public void checkOverdueMilestone(){
//        LocalDate today = LocalDate.now();
//        List<Projects> projects = projectsRepository.findAll();
//        for ( Projects project : projects){
//            try{
//                getTimeVariances(project.getId(), today);
//            }catch (Exception e){
//                System.out.println(e);
//            }
//        }
//    }
    @Transactional
    public MilestoneInfoDto createMilestone(UUID projectId, CreateMilestoneDto milestoneDto){
        return createMilestone(projectId, milestoneDto, null);
    }

    /**
     * Creates the milestone and its opening budget lines in one transaction, so a
     * failure on the budget does not leave a milestone behind without one.
     * A null or empty list creates the milestone with no budget lines.
     */
    @Transactional
    public MilestoneInfoDto createMilestone(UUID projectId, CreateMilestoneDto milestoneDto,
                                            List<CreateMilestoneBudgetLineDto> budgetRequest){
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(()-> new ResourceNotFoundException("Project not found"));
        Milestones newMilestone = milestonesMapper.toEntity(milestoneDto);
        newMilestone.setProject(project);
        newMilestone.setStatus(ProgressStatus.PENDING);
        milestonesRepository.save(newMilestone);

        if (budgetRequest != null && !budgetRequest.isEmpty()) {
            List<MilestoneBudget> lines = budgetRequest.stream()
                    .map(dto -> {
                        MilestoneBudget line = milestoneBudgetMapper.toEntity(dto);
                        line.setMilestone(newMilestone);
                        return line;
                    }).toList();
            milestoneBudgetRepository.saveAll(lines);
        }

        return (milestonesMapper.toInfoDto(newMilestone));
    }

    public MilestoneInfoDto getMilestone( UUID milestoneId){
        return (
                milestonesMapper.toInfoDto(
                        milestonesRepository.findById(milestoneId)
                                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"))
                )
                );
    }

    public List<MilestoneInfoDto> getPhaseMilestones(UUID phaseId){
        return (
                milestonesMapper.toInfoDtoList(
                        milestonesRepository.findAllByPhaseId(phaseId)
                )
                );
    }
    public Page<MilestoneInfoDto> getProjectMilestonesPage(UUID projectId, String search, Pageable pageable){
        return milestonesRepository.findAllByProjectId(projectId, search, pageable)
                .map(milestonesMapper::toInfoDto);
    }
    public MilestoneSummaryDto getMilestoneSummary(UUID projectId){
        if (!projectsRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        long total = milestonesRepository.countByProjectId(projectId);
        long overdue = milestonesRepository.countOverdueMilestones(projectId, LocalDate.now());
        long inProgress = milestonesRepository.countByProjectIdAndStatus(projectId, ProgressStatus.IN_PROGRESS);
        long completed = milestonesRepository.countByProjectIdAndStatus(projectId, ProgressStatus.COMPLETED);
        long pending = milestonesRepository.countByProjectIdAndStatus(projectId, ProgressStatus.PENDING);

        return new MilestoneSummaryDto(total, overdue, inProgress, completed, pending);
    }
    @Transactional
    public MilestoneInfoDto updateMilestone(UUID milestoneId, UpdateMilestoneDto dto){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        boolean durationChanged = dto.getDuration() != null && dto.getDuration() != milestone.getDuration();
        boolean hadStarted = milestone.getActualStartDate() != null;
        milestonesMapper.updateMilestone(dto, milestone);
        applyStatusFromDates(milestone);
        if (!hadStarted && milestone.getActualStartDate() != null && milestone.getPhase() != null) {
            phasesService.setPhaseActualStartDate(milestone.getPhase().getId());
        }
        if (milestone.getPhase() != null) {
            phasesService.syncPhaseBudget(milestone.getPhase().getId());
        }
        // A new duration moves the planned dates, so the baseline has to be worked out
        // again; that recalculation refreshes the forecast as well.
        if (durationChanged) {
            scheduleService.recalculateIfPossible(milestone.getProject().getId());
        } else {
            forecastService.recalculateForecastIfPossible(milestone.getProject().getId());
        }
        return milestonesMapper.toInfoDto(milestone);
    }
    // Actual dates are the source of truth for progress: a start means work is under
    // way, a start and an end mean it is done. With neither, the status is left alone.
    private void applyStatusFromDates(Milestones milestone) {
        if (milestone.getActualStartDate() != null && milestone.getActualEndDate() != null) {
            milestone.setStatus(ProgressStatus.COMPLETED);
        } else if (milestone.getActualStartDate() != null) {
            milestone.setStatus(ProgressStatus.IN_PROGRESS);
        }
    }
    @Transactional
    public void deleteMilestone(UUID milestoneId){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        UUID phaseId = milestone.getPhase() != null ? milestone.getPhase().getId() : null;
        UUID projectId = milestone.getProject().getId();

        scheduleRepository.deleteByMilestoneId(milestoneId);
        scheduleRepository.deleteByPredecessorId(milestoneId);
        milestonesRepository.delete(milestone);
        if (phaseId != null) {
            phasesService.syncPhaseBudget(phaseId);
        }
        scheduleService.recalculateIfPossible(projectId);
    }
    public MilestoneInfoDto getActiveMilestone(UUID projectId){
        LocalDate date  = LocalDate.now();
        List<Milestones> potentialMilestone = milestonesRepository.findActiveMilestoneByDate(projectId, date);
        // Need logic for situations where potentialMilestone returns null. For example if milestone A
        // ends on Friday and milestone B nds on Monday and the date is Saturday there os no milestone
        // scheduled so the query will return null

        return potentialMilestone.stream()
                .findFirst()
                .map(milestonesMapper::toInfoDto)
                .orElseThrow(()-> new ResourceNotFoundException("No active milestone found for date: "+ date));
    }

//    public List<TimeVarianceDto> getTimeVariances(UUID projectId, LocalDate currentDate){
//        List<Milestones> overdueMilestones = milestonesRepository.findOverdueMilestones(projectId, currentDate);
//
//        return overdueMilestones.stream()
//                .map(entity ->{
//                    long daysOverdue = ChronoUnit.DAYS.between(entity.getPlannedEndDate(), currentDate);
//                    TimeVarianceDto dto = new TimeVarianceDto();
//                    dto.setId(entity.getId());
//                    dto.setTitle(entity.getTitle());
//                    dto.setPlannedEndDate(entity.getPlannedEndDate());
//                    dto.setDaysOverdue(daysOverdue);
//                    dto.setSeverity(daysOverdue > 7 ? "CRITICAL":"WARNING");
//
//                    return dto;
//                }).collect(Collectors.toList());
//    }
    @Transactional
    public void setMilestoneActualStartDate(Milestones milestone){
        if(milestone.getActualStartDate() == null){
            milestone.setActualStartDate(LocalDate.now());
            milestone.setStatus(ProgressStatus.IN_PROGRESS);
            milestonesRepository.save(milestone);

            if (milestone.getPhase() != null) {
                phasesService.setPhaseActualStartDate(milestone.getPhase().getId());
            }
            forecastService.recalculateForecastIfPossible(milestone.getProject().getId());
        }
    }
    /**
     * Milestones that attendance on {@code date} can be booked against. Everything except a
     * milestone already finished before that date is offered; the ones that look active on
     * the date are flagged as suggested and listed first.
     */
    @Transactional
    public List<PossibleActiveMilestonesDto> getEligibleMilestones(UUID projectId, LocalDate date){
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("Project not found"));

        Map<UUID, List<Milestones>> predecessorsByMilestone = new HashMap<>();
        for (MilestoneDependency dependency : scheduleRepository.findByProjectId(project.getId())) {
            predecessorsByMilestone
                    .computeIfAbsent(dependency.getMilestone().getId(), id -> new ArrayList<>())
                    .add(dependency.getPredecessor());
        }

        List<PossibleActiveMilestonesDto> result = new ArrayList<>();
        Map<UUID, LocalDate> earliestStarts = new HashMap<>();
        for (Milestones m : milestonesRepository.findAllByProjectId(project.getId())) {
            if (m.getActualEndDate() != null && m.getActualEndDate().isBefore(date)) {
                continue;
            }
            List<Milestones> predecessors = predecessorsByMilestone.getOrDefault(m.getId(), List.of());
            boolean hasActivePredecessor = predecessors.stream()
                    .anyMatch(pred -> pred.getStatus() != ProgressStatus.COMPLETED);

            PossibleActiveMilestonesDto dto = new PossibleActiveMilestonesDto();
            dto.setId(m.getId());
            dto.setTitle(m.getTitle());
            dto.setPhaseTitle(m.getPhase() != null ? m.getPhase().getTitle() : null);
            String reason = determineMatchReason(m, date, hasActivePredecessor);
            dto.setMatchReason(reason);
            dto.setSuggested(reason != null);
            if (m.getActualStartDate() == null && hasActivePredecessor) {
                dto.setWarningMessage("The milestone has active predecessors. Are you sure you want to select it " +
                        "before completing the predecessor?");
            }
            earliestStarts.put(m.getId(), m.getEarliestStart());
            result.add(dto);
        }

        result.sort(Comparator
                .comparing(PossibleActiveMilestonesDto::isSuggested).reversed()
                .thenComparing(dto -> earliestStarts.get(dto.getId()), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(PossibleActiveMilestonesDto::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));
        return result;
    }

    /** Why a milestone looks active on {@code date}, or null when it does not. First match wins. */
    private String determineMatchReason(Milestones milestone, LocalDate date, boolean hasActivePredecessor){
        if (milestone.getActualStartDate() != null && milestone.getActualEndDate() == null) {
            return "In progress";
        }
        if (milestone.getActualEndDate() != null) {
            // Finished on or after the date, or it would have been excluded.
            return "Active on this date";
        }
        LocalDate start = milestone.getEarliestStart();
        LocalDate finish = milestone.getLatestFinish();
        if (start != null && finish != null && !date.isBefore(start) && !date.isAfter(finish)) {
            return "Scheduled for this date";
        }
        if (milestone.getActualStartDate() == null && !hasActivePredecessor) {
            return "Ready to start";
        }
        return null;
    }
}
