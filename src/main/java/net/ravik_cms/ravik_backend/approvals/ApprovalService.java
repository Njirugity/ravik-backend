package net.ravik_cms.ravik_backend.approvals;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;
    private final ProjectsRepository projectsRepository;
    private final ApprovalMapper approvalMapper;

    @Transactional
    public ApprovalInfoDto createApproval(CreateApprovalDto request, UUID projectId){
        Projects project = projectsRepository.findById(projectId).orElseThrow(()->
                new ResourceNotFoundException("Project not found"));
        Approval approval = approvalMapper.toEntity(request);
        approval.setProject(project);
        approvalRepository.save(approval);
        return approvalMapper.toInfoDto(approval);
    }

    public ApprovalInfoDto getApproval(Long id){
        Approval approval = approvalRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Approval not found"));
        return approvalMapper.toInfoDto(approval);
    }

    public Page<ApprovalInfoDto> getApprovals(UUID projectId, String search, Pageable pageable){
        return approvalRepository.findAllApprovals(projectId, search, pageable)
                .map(approvalMapper::toInfoDto);
    }

    @Transactional
    public ApprovalInfoDto updateApproval(Long id, UpdateApprovalDto request){
        Approval approval = approvalRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Approval not found"));
        approvalMapper.updateApprovalFromDto(request, approval);
        return approvalMapper.toInfoDto(approvalRepository.save(approval));
    }

    @Transactional
    public void deleteApproval(Long id){
        Approval approval = approvalRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Approval not found"));
        approvalRepository.delete(approval);
    }
}
