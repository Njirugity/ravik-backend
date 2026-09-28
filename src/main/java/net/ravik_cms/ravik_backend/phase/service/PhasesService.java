package net.ravik_cms.ravik_backend.phase.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneBudget.repository.MilestoneBudgetRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.phase.dtos.*;
import net.ravik_cms.ravik_backend.phase.entity.Phases;
import net.ravik_cms.ravik_backend.phase.mapper.PhasesMapper;
import net.ravik_cms.ravik_backend.phase.repository.PhasesRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhasesService {
    private final PhasesMapper phasesMapper;
    private final PhasesRepository phasesRepository;
    private final ProjectsRepository projectsRepository;
    private final MilestonesRepository milestonesRepository;
    private final MilestoneBudgetRepository milestoneBudgetRepository;

    @Transactional
    public PhasesInfoDto createPhase(UUID projectId, CreatePhaseDto request){
        Projects project = projectsRepository.findById(projectId).
                orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        Phases phase = phasesMapper.fromCreatePhaseDto(request);
        phase.setProject(project);
        phase.setStatus(ProgressStatus.PENDING);
        phasesRepository.save(phase);
        return phasesMapper.toPhaseInfoDto(phase);
    }

    public PhasesInfoDto getPhase(UUID phaseId) {
        PhasesInfoDto phasesInfoDto =  phasesMapper.toPhaseInfoDto(phasesRepository.findById(phaseId).
                orElseThrow(() -> new ResourceNotFoundException("Phase not found")));
        phasesInfoDto.setTotalMilestones(milestonesRepository.countByPhaseId(phaseId));
        phasesInfoDto.setTotalMilestonesCompleted(milestonesRepository.
                countByPhaseIdAndCompleted(phaseId));
        return phasesInfoDto;
    }

    public Page<PhasesInfoDto> getPhases(UUID projectId, String search, Pageable pageable) {
        Page<Phases> phases = phasesRepository.findAllByProjectId(projectId, search, pageable);
        return phases.map(phase -> {
            PhasesInfoDto dto = phasesMapper.toPhaseInfoDto(phase);
            dto.setTotalMilestones(milestonesRepository.countByPhaseId(dto.getId()));
            dto.setTotalMilestonesCompleted(milestonesRepository.
                    countByPhaseIdAndCompleted(dto.getId()));
            return dto;
        });
    }
    @Transactional
    public PhasesInfoDto updatePhase(UUID phaseId, UpdatePhaseDto request){
        Phases phase = phasesRepository.findById(phaseId).
                orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
        phasesMapper.UpdatePhase(request, phase);
        return phasesMapper.toPhaseInfoDto(phase);
    }

    @Transactional
    public void deletePhase(UUID phaseId){
        Phases phase = phasesRepository.findById(phaseId).
                orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
        phasesRepository.delete(phase);
    }
    @Transactional
    public void assignMilestones(UUID phaseId, List<MilestoneAssignmentDto> request) {
        Phases phase = phasesRepository.findById(phaseId).
                orElseThrow(() -> new ResourceNotFoundException("Phase not found"));
        List<UUID> milestoneIds = request.stream()
                .map(MilestoneAssignmentDto::getMilestoneId)
                .toList();
        List<Milestones> milestones = milestonesRepository.findAllById(milestoneIds);
        if (milestones.size() != milestoneIds.size()) {
            Set<UUID> found = milestones.stream().map(Milestones::getId).collect(Collectors.toSet());
            List<UUID> missing = milestoneIds.stream().filter(id -> !found.contains(id)).toList();
            throw new ResourceNotFoundException("Milestones not found: " + missing);
        }
        Set<UUID> affectedPhaseIds = new HashSet<>();
        affectedPhaseIds.add(phaseId);
        for (Milestones m : milestones) {
            if (m.getPhase() != null) {
                affectedPhaseIds.add(m.getPhase().getId());
            }
            m.setPhase(phase);
        }
        milestonesRepository.flush();
        affectedPhaseIds.forEach(this::syncPhaseBudget);
    }
    @Transactional
    public void unassignMilestones(List<MilestoneAssignmentDto> request){
         List<UUID> milestoneIds = request.stream()
                 .map(MilestoneAssignmentDto::getMilestoneId)
                 .toList();
         List<Milestones> milestones = milestonesRepository.findAllById(milestoneIds);
         if(milestones.size() != milestoneIds.size()) {
             Set<UUID> found = milestones.stream().map(Milestones::getId).collect(Collectors.toSet());
             List<UUID> missing = milestoneIds.stream().filter(id -> !found.contains(id)).toList();
             throw new ResourceNotFoundException("Milestones not found: " + missing);
         }
         Set<UUID> affectedPhaseIds = new HashSet<>();
         for(Milestones m : milestones){
             if (m.getPhase() != null) {
                 affectedPhaseIds.add(m.getPhase().getId());
             }
             m.setPhase(null);
         }
         milestonesRepository.flush();
         affectedPhaseIds.forEach(this::syncPhaseBudget);
    }
    @Transactional
    public void syncPhaseBudget(UUID phaseId){
        Phases phase = phasesRepository.findById(phaseId)
                .orElseThrow();
        Double totalPhaseBudget = milestoneBudgetRepository.sumAmountByPhaseId(phaseId);
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
        boolean isOverdue = milestonesRepository.existsByPhaseIdAndStatusNotAndEarliestFinishBefore(
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
