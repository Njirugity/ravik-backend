package net.ravik_cms.ravik_backend.subContractor.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.subContractor.dtos.CreateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.dtos.SubContractorInfoProjection;
import net.ravik_cms.ravik_backend.subContractor.dtos.UpdateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;
import net.ravik_cms.ravik_backend.subContractor.mapper.SubContractorMapper;
import net.ravik_cms.ravik_backend.subContractor.repository.SubContractorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubContractorService {
    private final SubContractorRepository subContractorRepository;
    private final ProjectsRepository projectsRepository;
    private final SubContractorMapper subContractorMapper;

    public void addSubContractor(CreateSubContractorDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        SubContractor subContractor = subContractorMapper.toEntity(request);
        subContractor.setProject(project);
        subContractorRepository.save(subContractor);
    }

    public Page<SubContractorInfoProjection> getSubContractors(UUID projectId, String search, String jobType, Pageable pageable) {
        return subContractorRepository.findAllSubContractors(projectId, search, jobType, pageable);
    }

    public void updateSubContractor(Long id, UpdateSubContractorDto request) {
        SubContractor subContractor = subContractorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor not found"));
        subContractorMapper.updateSubContractor(request, subContractor);
        subContractorRepository.save(subContractor);
    }

    public void deleteSubContractor(Long id) {
        SubContractor subContractor = subContractorRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Sub contractor not found"));
        subContractorRepository.delete(subContractor);
    }
}
