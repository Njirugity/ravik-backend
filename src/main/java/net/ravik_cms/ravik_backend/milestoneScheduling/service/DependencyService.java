package net.ravik_cms.ravik_backend.milestoneScheduling.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.CircularDependencyException;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.DependenciesDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.DependencyDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.dtos.ProjectDependenciesDto;
import net.ravik_cms.ravik_backend.milestoneScheduling.entity.MilestoneDependency;
import net.ravik_cms.ravik_backend.milestoneScheduling.repository.ScheduleRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DependencyService {
    private final ScheduleRepository scheduleRepository;
    private final MilestonesRepository milestonesRepository;
    private final ScheduleService scheduleService;

    public void addPredecessor(UUID milestoneId, UUID predecessorId){
        if (milestoneId.equals(predecessorId)) {
            throw new IllegalArgumentException("A milestone cannot depend on itself");
        }

        Milestones milestone =  milestonesRepository.findById(milestoneId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        Milestones predecessor = milestonesRepository.findById(predecessorId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        Projects milestoneProject = milestone.getProject();
        Projects predecessorProject = predecessor.getProject();
        if (!milestoneProject.getId().equals(predecessorProject.getId())) {
            throw new IllegalArgumentException("Milestone and Predecessor must belong to the same project.");
        }

        if (scheduleRepository.existsByMilestoneIdAndPredecessorId(milestoneId, predecessorId)) {
            throw new CircularDependencyException("Adding this predecessor would create a cycle");
        }
        if (wouldCreateCycle(milestoneId, predecessorId)) {
            throw new CircularDependencyException("Adding this predecessor would create a cycle");
        }

        MilestoneDependency dependencies = new MilestoneDependency();
        dependencies.setMilestone(milestone);
        dependencies.setPredecessor(predecessor);
        dependencies.setProject(milestoneProject);
        scheduleRepository.save(dependencies);

        scheduleService.recalculateIfPossible(milestoneProject.getId());
    }

    private boolean wouldCreateCycle(UUID milestoneId, UUID predecessorId){
        return hasPath(predecessorId, milestoneId, new HashSet<>());
    }
    private boolean hasPath(UUID fromId, UUID toId, Set<UUID> visited){
        if(fromId.equals(toId)) return true;
        if(visited.contains(fromId)) return false;

        visited.add(fromId);
        List<Milestones> successors = scheduleRepository.findSuccessorByMilestoneId(fromId);
        for(Milestones successor: successors){
            if(hasPath(successor.getId(), toId, visited)){
                return true;
            }
        }
        return false;

    }
    public void removePredecessor(UUID milestoneId, UUID predecessorId){
        Milestones milestone =  milestonesRepository.findById(milestoneId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        Milestones predecessor = milestonesRepository.findById(predecessorId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        if (!scheduleRepository.existsByMilestoneIdAndPredecessorId(milestoneId, predecessorId)) {
            throw new ResourceNotFoundException("Predecessor relationship not found");
        }
        scheduleRepository.deleteByMilestoneIdAndPredecessorId(milestoneId, predecessorId);

        scheduleService.recalculateIfPossible(milestone.getProject().getId());
    }

    public List<DependencyDto> getPredecessors(UUID milestoneId){
        List<Milestones> predecessors = scheduleRepository.findPredecessorByMilestoneId(milestoneId);
        return predecessors.stream()
                .map(p->new DependencyDto(p.getId(), p.getTitle()))
                .toList();

    }
    public List<DependencyDto> getSuccessors(UUID milestoneId){
        List<Milestones> successors = scheduleRepository.findSuccessorByMilestoneId(milestoneId);
        return successors.stream()
                .map(s->new DependencyDto(s.getId(), s.getTitle()))
                .toList();
    }
    public List<DependenciesDto> getMilestoneDependents(UUID projectId){
        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);
        List<UUID> milestoneIds = milestones.stream()
                .map(Milestones::getId)
                .toList();
        List<MilestoneDependency> dependencies = scheduleRepository.findByMilestoneIdIn(milestoneIds);
        Map<UUID, List<MilestoneDependency>> grouped = dependencies.stream()
                .collect(Collectors.groupingBy(d->d.getMilestone().getId()));
        return milestones.stream()
                .map(m->{
                    List<MilestoneDependency> deps = grouped.getOrDefault(m.getId(), new ArrayList<>());
                    List<DependencyDto> dependencyDto = deps.stream()
                            .map(d->new DependencyDto(
                                    d.getPredecessor().getId(),
                                    d.getPredecessor().getTitle()
                            ))
                            .toList();

                    return new DependenciesDto(
                            m.getId(), m.getTitle(), m.getDescription(),
                            m.getPhase() != null ? m.getPhase().getTitle() : null,
                            m.getDuration(), dependencyDto
                    );
                })
                .toList();
    }
    public void updateAllPredecessors(UUID milestoneId, List<UUID> predecessorIds) {
        Milestones milestone = milestonesRepository.findById(milestoneId).
                orElseThrow(()-> new ResourceNotFoundException("Milestone not found"));
        scheduleRepository.deleteByMilestoneId(milestoneId);

        for (UUID predId : predecessorIds) {
            addPredecessor(milestoneId, predId);
        }
        // Ensures the schedule still recalculates when predecessorIds is empty
        // (i.e. all predecessors were cleared), a case the loop above never reaches.
        scheduleService.recalculateIfPossible(milestone.getProject().getId());
    }
    public ProjectDependenciesDto getProjectDependencies(UUID projectId){
        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);

        ProjectDependenciesDto pdd =  new ProjectDependenciesDto();
        pdd.setProjectId(projectId);

        for (Milestones milestone: milestones){
            DependenciesDto milDep = new DependenciesDto();
            milDep.setMilestoneId(milestone.getId());
            milDep.setMilestoneTitle(milestone.getTitle());
            milDep.setMilestoneDescription(milestone.getDescription());
            milDep.setDuration(milestone.getDuration());

            List<Milestones> predecessors = scheduleRepository.findPredecessorByMilestoneId(milestone.getId());
            for (Milestones pred : predecessors){
                milDep.getDependencies().add(new DependencyDto(pred.getId(), pred.getTitle()));
            }
            pdd.getDependencies().add(milDep);
        }

        return pdd;
    }
}
