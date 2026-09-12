package net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.CreateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.SubContractorRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.UpdateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.mapper.SubContractorRequiredMapper;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.repository.SubContractorRequiredRepository;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;
import net.ravik_cms.ravik_backend.subContractor.repository.SubContractorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubContractorRequiredService {
    private final SubContractorRequiredRepository subContractorRequiredRepository;
    private final MilestonesRepository milestonesRepository;
    private final SubContractorRepository subContractorRepository;
    private final SubContractorRequiredMapper subContractorRequiredMapper;

    @Transactional
    public List<SubContractorRequiredInfoProjection> createSubContractorRequired(UUID milestoneId, List<CreateSubContractorRequiredDto> requests) {
        Milestones milestone = milestonesRepository.findById(milestoneId).orElseThrow(() ->
                new ResourceNotFoundException("Milestone not found"));

        List<SubContractorRequired> entries = requests.stream()
                .map(dto -> {
                    SubContractor subContractor = subContractorRepository.findById(dto.getSubContractorId()).orElseThrow(() ->
                            new ResourceNotFoundException("Sub contractor not found"));
                    SubContractorRequired entry = subContractorRequiredMapper.toEntity(dto);
                    entry.setSubContractor(subContractor);
                    entry.setMilestone(milestone);
                    entry.setProject(milestone.getProject());
                    return entry;
                }).toList();

        subContractorRequiredRepository.saveAll(entries);
        return getSubContractorRequired(milestoneId);
    }

    public List<SubContractorRequiredInfoProjection> getSubContractorRequired(UUID milestoneId) {
        return subContractorRequiredRepository.findAllByMilestoneId(milestoneId);
    }

    public Page<SubContractorRequiredInfoProjection> getSubContractorRequiredByProject(
            UUID projectId, String search, String jobType, Pageable pageable) {
        return subContractorRequiredRepository.findAllByProjectId(projectId, search, jobType, pageable);
    }

    public void updateSubContractorRequired(Long id, UpdateSubContractorRequiredDto request) {
        SubContractorRequired entry = subContractorRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor required entry not found"));
        if (request.getSubContractorId() != null) {
            SubContractor subContractor = subContractorRepository.findById(request.getSubContractorId()).orElseThrow(() ->
                    new ResourceNotFoundException("Sub contractor not found"));
            entry.setSubContractor(subContractor);
        }
        subContractorRequiredMapper.updateSubContractorRequired(request, entry);
        subContractorRequiredRepository.save(entry);
    }

    public void deleteSubContractorRequired(Long id) {
        SubContractorRequired entry = subContractorRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor required entry not found"));
        subContractorRequiredRepository.delete(entry);
    }
}
