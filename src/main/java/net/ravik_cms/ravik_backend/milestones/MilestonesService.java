package net.ravik_cms.ravik_backend.milestones;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.phase.Phases;
import net.ravik_cms.ravik_backend.phase.PhasesRepository;
import net.ravik_cms.ravik_backend.phase.PhasesService;
import net.ravik_cms.ravik_backend.phase.UpdatePhaseDto;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MilestonesService {
    private final MilestonesRepository milestonesRepository;
    private final PhasesRepository phasesRepository;
    private final MilestonesMapper milestonesMapper;
    private final ProjectsRepository projectsRepository;
    private final PhasesService phasesService;

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
    public MilestoneInfoDto createMilestone(UUID phaseId, CreateMilestoneDto milestoneDto){
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow(()-> new ResourceNotFoundException("Phase not found"));
        Milestones newMilestone = milestonesMapper.toEntity(milestoneDto);
        newMilestone.setPhase(phase);
        newMilestone.setProject(phase.getProject());
        newMilestone.setStatus(ProgressStatus.PENDING);
        milestonesRepository.save(newMilestone);
        phasesService.syncPhaseBudget(phaseId);
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
    public List<MilestoneInfoDto> getProjectMilestones(UUID projectId){
        return (
                milestonesMapper.toInfoDtoList(
                        milestonesRepository.findAllByProjectId(projectId)
                )
                );
    }
    @Transactional
    public MilestoneInfoDto updateMilestone(UUID milestoneId, UpdateMilestoneDto dto){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        milestonesMapper.updateMilestone(dto, milestone);
        phasesService.syncPhaseBudget(milestone.getPhase().getId());
        return milestonesMapper.toInfoDto(milestone);
    }
    public void deleteMilestone(UUID milestoneId){
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        milestonesRepository.delete(milestone);
        phasesService.syncPhaseBudget(milestone.getPhase().getId());
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
}
