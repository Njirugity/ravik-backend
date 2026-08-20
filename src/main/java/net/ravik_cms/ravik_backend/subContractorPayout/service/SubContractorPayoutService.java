package net.ravik_cms.ravik_backend.subContractorPayout.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.entity.SubContractorRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.repository.SubContractorRequiredRepository;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.CreateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.SubContractorPayoutInfoProjection;
import net.ravik_cms.ravik_backend.subContractorPayout.dtos.UpdateSubContractorPayoutDto;
import net.ravik_cms.ravik_backend.subContractorPayout.entity.SubContractorPayout;
import net.ravik_cms.ravik_backend.subContractorPayout.mapper.SubContractorPayoutMapper;
import net.ravik_cms.ravik_backend.subContractorPayout.repository.SubContractorPayoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubContractorPayoutService {
    private final SubContractorPayoutRepository subContractorPayoutRepository;
    private final MilestonesRepository milestonesRepository;
    private final SubContractorRequiredRepository subContractorRequiredRepository;
    private final SubContractorPayoutMapper subContractorPayoutMapper;

    @Transactional
    public List<SubContractorPayoutInfoProjection> createSubContractorPayout(UUID milestoneId, CreateSubContractorPayoutDto request) {
        Milestones milestone = milestonesRepository.findById(milestoneId).orElseThrow(() ->
                new ResourceNotFoundException("Milestone not found"));
        SubContractorRequired subContractorRequired = subContractorRequiredRepository.findById(request.getSubContractorRequiredId()).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor required entry not found"));

        SubContractorPayout payout = subContractorPayoutMapper.toEntity(request);
        payout.setSubContractorRequired(subContractorRequired);
        payout.setMilestone(milestone);
        payout.setProject(milestone.getProject());

        subContractorPayoutRepository.save(payout);
        return getSubContractorPayout(milestoneId);
    }

    public List<SubContractorPayoutInfoProjection> getSubContractorPayout(UUID milestoneId) {
        return subContractorPayoutRepository.findAllByMilestoneId(milestoneId);
    }

    public Page<SubContractorPayoutInfoProjection> getByProject(
            UUID projectId, String search, LocalDate jobDate, Pageable pageable) {
        return subContractorPayoutRepository.findAllByProjectId(projectId, search, jobDate, pageable);
    }

    public void updateSubContractorPayout(Long id, UpdateSubContractorPayoutDto request) {
        SubContractorPayout payout = subContractorPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor payout entry not found"));
        if (request.getSubContractorRequiredId() != null) {
            SubContractorRequired subContractorRequired = subContractorRequiredRepository.findById(request.getSubContractorRequiredId()).orElseThrow(() ->
                    new ResourceNotFoundException("Sub contractor required entry not found"));
            payout.setSubContractorRequired(subContractorRequired);
        }
        subContractorPayoutMapper.updateSubContractorPayout(request, payout);
        subContractorPayoutRepository.save(payout);
    }

    public void deleteSubContractorPayout(Long id) {
        SubContractorPayout payout = subContractorPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor payout entry not found"));
        subContractorPayoutRepository.delete(payout);
    }
}
