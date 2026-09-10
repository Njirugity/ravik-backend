package net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.jobTitles.JobTitles;
import net.ravik_cms.ravik_backend.jobTitles.JobTitlesRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.CreateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.LaborRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.UpdateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.entity.LaborRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.mapper.LaborRequiredMapper;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.repository.LaborRequiredRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LaborRequiredService {
    private final LaborRequiredRepository laborRequiredRepository;
    private final MilestonesRepository milestonesRepository;
    private final JobTitlesRepository jobTitlesRepository;
    private final LaborRequiredMapper laborRequiredMapper;

    @Transactional
    public List<LaborRequiredInfoProjection> createLaborRequired(UUID milestoneId, List<CreateLaborRequiredDto> requests) {
        Milestones milestone = milestonesRepository.findById(milestoneId).orElseThrow(() ->
                new ResourceNotFoundException("Milestone not found"));

        List<LaborRequired> entries = requests.stream()
                .map(dto -> {
                    JobTitles jobTitle = jobTitlesRepository.findById(dto.getJobTitleId()).orElseThrow(() ->
                            new ResourceNotFoundException("Job title not found"));
                    LaborRequired entry = laborRequiredMapper.toEntity(dto);
                    entry.setJobTitle(jobTitle);
                    entry.setMilestone(milestone);
                    entry.setProject(milestone.getProject());
                    return entry;
                }).toList();

        laborRequiredRepository.saveAll(entries);
        return getLaborRequired(milestoneId);
    }

    public List<LaborRequiredInfoProjection> getLaborRequired(UUID milestoneId) {
        return laborRequiredRepository.findAllByMilestoneId(milestoneId);
    }

    public void updateLaborRequired(Long id, UpdateLaborRequiredDto request) {
        LaborRequired entry = laborRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Labor required entry not found"));
        if (request.getJobTitleId() != null) {
            JobTitles jobTitle = jobTitlesRepository.findById(request.getJobTitleId()).orElseThrow(() ->
                    new ResourceNotFoundException("Job title not found"));
            entry.setJobTitle(jobTitle);
        }
        laborRequiredMapper.updateLaborRequired(request, entry);
        laborRequiredRepository.save(entry);
    }

    public void deleteLaborRequired(Long id) {
        LaborRequired entry = laborRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Labor required entry not found"));
        laborRequiredRepository.delete(entry);
    }
}
