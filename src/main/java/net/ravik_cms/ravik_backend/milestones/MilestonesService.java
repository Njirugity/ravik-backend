package net.ravik_cms.ravik_backend.milestones;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.DateStatus;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneScheduling.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestoneScheduling.ScheduleService;
import net.ravik_cms.ravik_backend.phase.Phases;
import net.ravik_cms.ravik_backend.phase.PhasesRepository;
import net.ravik_cms.ravik_backend.phase.PhasesService;
import net.ravik_cms.ravik_backend.phase.UpdatePhaseDto;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    public MilestoneInfoDto createMilestone(UUID phaseId, CreateMilestoneDto milestoneDto){
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow(()-> new ResourceNotFoundException("Phase not found"));
        Milestones newMilestone = milestonesMapper.toEntity(milestoneDto);
        newMilestone.setPhase(phase);
        newMilestone.setProject(phase.getProject());
        newMilestone.setStatus(ProgressStatus.PENDING);
        milestonesRepository.save(newMilestone);
        phasesService.syncPhaseBudget(phaseId);

        scheduleService.recalculateIfPossible(phase.getProject().getId());

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
    @Transactional
    public MilestoneInfoDto updateMilestone(UUID milestoneId, UpdateMilestoneDto dto){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        milestonesMapper.updateMilestone(dto, milestone);
        phasesService.syncPhaseBudget(milestone.getPhase().getId());
        return milestonesMapper.toInfoDto(milestone);
    }
    @Transactional
    public void deleteMilestone(UUID milestoneId){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        UUID phaseId = milestone.getPhase().getId();
        UUID projectId = milestone.getProject().getId();

        scheduleRepository.deleteByMilestoneId(milestoneId);
        scheduleRepository.deleteByPredecessorId(milestoneId);
        milestonesRepository.delete(milestone);
        phasesService.syncPhaseBudget(phaseId);
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

            phasesService.setPhaseActualStartDate(milestone.getPhase().getId());
        }
    }
    @Transactional
    public List<PossibleActiveMilestonesDto> getEligibleMilestones(UUID projectId){
        LocalDate today = LocalDate.now();
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("Project not found"));
        List<Milestones> condition1 = milestonesRepository.findByActualStartDateIsNullAndDateBetweenESAnsLF(today, project.getId());
        List<Milestones> condition2 = milestonesRepository.findByDateBetweenESAndLF(today, project.getId());
        List<Milestones> condition3 = milestonesRepository.findWithNoPredecessorsAndNoActualStart(project.getId());

        Map<UUID, Milestones> uniqueMilestones = new LinkedHashMap<>();

        condition1.forEach(m -> uniqueMilestones.put(m.getId(), m));
        condition2.forEach(m -> uniqueMilestones.put(m.getId(), m));
        condition3.forEach(m -> uniqueMilestones.put(m.getId(), m));

        return processMilestonesMatches(new ArrayList<>(uniqueMilestones.values()));
    }
    private boolean checkActivePredecessors(Milestones milestone){
        List<Milestones> predecessors = scheduleRepository.findPredecessorByMilestoneId(milestone.getId());
        for(Milestones pred: predecessors){
            if(pred.getStatus() != ProgressStatus.COMPLETED){
                return true;
            }
        }
        return false;
    }

    private String determineMatchReason(Milestones milestone){
        LocalDate today = LocalDate.now();
        if (milestone.getActualStartDate() != null &&
                milestone.getActualEndDate() == null) {
            return "Started but not completed";
        }

        if (milestone.getActualStartDate() == null &&
                today.isAfter(milestone.getEarliestStart()) &&
                today.isBefore(milestone.getLatestFinish())) {
            return "Not started but within date range";
        }

        if (today.isAfter(milestone.getEarliestStart()) &&
                today.isBefore(milestone.getLatestFinish())) {
            return "Within date range";
        }
        return "other";
    }
    private List<PossibleActiveMilestonesDto> processMilestonesMatches(List<Milestones>milestones){
        List<PossibleActiveMilestonesDto> result = new ArrayList<>();

        for (Milestones m : milestones){
            PossibleActiveMilestonesDto dto = new PossibleActiveMilestonesDto();
            dto.setId(m.getId());
            dto.setTitle(m.getTitle());

            boolean hasActivePredecessor = checkActivePredecessors(m);
            if(hasActivePredecessor){
                dto.setWarningMessage("The milestone has active predecessors. Are you sure you want to select it" +
                        "before completing the predecessor?");
            }
            String reason = determineMatchReason(m);
            dto.setMatchReason(reason);

            result.add(dto);
        }
        return result;
    }
}
