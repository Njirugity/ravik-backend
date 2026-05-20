package net.ravik_cms.ravik_backend.phase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhasesService {
    private final PhasesMapper phasesMapper;
    private final PhasesRepository phasesRepository;
    private final ProjectsRepository projectsRepository;
    private final MilestonesRepository milestonesRepository;

    @Transactional
    public PhasesInfoDto createPhase(UUID projectId, CreatePhaseDto request){
        Projects project = projectsRepository.findById(projectId).
                orElseThrow(() -> new RuntimeException("Project not found"));
        Phases phase = phasesMapper.fromCreatePhaseDto(request);
        phase.setProject(project);
        phase.setStatus(ProgressStatus.PENDING);
        phasesRepository.save(phase);
        return phasesMapper.toPhaseInfoDto(phase);
    }

    public PhasesInfoDto getPhase(UUID phaseId) {
        PhasesInfoDto phasesInfoDto =  phasesMapper.toPhaseInfoDto(phasesRepository.findById(phaseId).
                orElseThrow(() -> new RuntimeException("Phase not found")));
        phasesInfoDto.setTotalMilestones(milestonesRepository.countByPhaseId(phaseId));
        phasesInfoDto.setTotalMilestonesCompleted(milestonesRepository.
                countByPhaseIdAndCompleted(phaseId));
        return phasesInfoDto;
    }

    public List<PhasesInfoDto> getPhases(UUID projectId) {
        List<Phases> phases = phasesRepository.findAllByProjectId(projectId);
        List<PhasesInfoDto> phaseInfoDto = phasesMapper.toPhaseInfoList(phases);
        return phaseInfoDto.stream()
                .map(p->{
                    p.setTotalMilestones(milestonesRepository.countByPhaseId(p.getId()));
                    p.setTotalMilestonesCompleted(milestonesRepository.
                            countByPhaseIdAndCompleted(p.getId()));
                    return p;
                }).toList();

    }
    @Transactional
    public PhasesInfoDto updatePhase(UUID phaseId, UpdatePhaseDto request){
        Phases phase = phasesRepository.findById(phaseId).
                orElseThrow(() -> new RuntimeException("Phase not found"));
        phasesMapper.UpdatePhase(request, phase);
        return phasesMapper.toPhaseInfoDto(phase);
    }

    @Transactional
    public void deletePhase(UUID phaseId){
        Phases phase = phasesRepository.findById(phaseId).
                orElseThrow(() -> new RuntimeException("Phase not found"));
        phasesRepository.delete(phase);
    }
    @Transactional
    public void syncPhaseBudget(UUID phaseId){
        Double totalPhaseBudget = milestonesRepository.phaseBudget(phaseId);
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow();

        phase.setBudget(totalPhaseBudget != null ? totalPhaseBudget : 0.0);
    }
    public PhasesInfoDto getActivePhase(UUID projectId){
        List<Phases> activePhase= phasesRepository.findActivePhases(projectId);
        PhasesInfoDto phasesInfoDto = activePhase.stream()
                .findFirst()
                .map(phasesMapper::toPhaseInfoDto)
                .orElseThrow(()->new ResourceNotFoundException("No active phase"));
        phasesInfoDto.setTotalMilestones(milestonesRepository.countByPhaseId(phasesInfoDto.getId()));
        phasesInfoDto.setTotalMilestonesCompleted(milestonesRepository.
                countByPhaseIdAndCompleted(phasesInfoDto.getId()));
        return phasesInfoDto;
    }
    public PhaseOverdueDto overduePhases(UUID phaseId){
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow(()-> new ResourceNotFoundException("Phase not found"));
        boolean isOverdue = milestonesRepository.existsByPhaseIdAndStatusNotAndPlannedEndDateBefore(
                phaseId,
                ProgressStatus.COMPLETED,
                LocalDate.now()
        );
        PhaseOverdueDto overdue = new PhaseOverdueDto();
        overdue.setId(phaseId);
        overdue.setTitle(phase.getTitle());
        overdue.setSeverity(isOverdue ? "AT_RISK":"ON_TRACK");
        return overdue;
    }
    @Transactional
    public void setPhaseActualStartDate(UUID phaseId){
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow(()-> new ResourceNotFoundException("Phase not found"));
        if (phase.getActualStartDate() == null) {
            Optional<LocalDate> earliestStart = milestonesRepository.findMinActualStartDateByPhaseId(phaseId);

            earliestStart.ifPresent(phase::setActualStartDate);
            phase.setStatus(ProgressStatus.IN_PROGRESS);
        }
    }
    public PhasesInfographicsDto getInfographicForPhase(UUID projectId){
        PhasesInfoDto activePhase = getActivePhase(projectId);
        LocalDate projectPlannedEndDate = phasesRepository.findProjectPlannedEndDate(projectId)
                .orElseThrow(()-> new ResourceNotFoundException(("Planned End Date not Found")));
        Long totalPhases = phasesRepository.countByProjectId(projectId)
                .orElseThrow(()-> new ResourceNotFoundException(("Project has no phases")));
        PhasesInfographicsDto dto =  new PhasesInfographicsDto();
        dto.setTotalPhases(totalPhases);
        dto.setActivePhase(activePhase.getTitle());
        dto.setPlannedEndDate(projectPlannedEndDate);
        dto.setTotalSpent(0.0);
        return dto;
    }
}
