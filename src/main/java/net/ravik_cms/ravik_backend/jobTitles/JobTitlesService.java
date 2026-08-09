package net.ravik_cms.ravik_backend.jobTitles;

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
public class JobTitlesService {
    private final JobTitlesRepository jobTitlesRepository;
    private final ProjectsRepository projectsRepository;
    private final JobTitleMapper jobTitleMapper;

    public void addJobTitle(CreateJobTitleDto request, UUID projectId){
        Projects projects = projectsRepository.findById(projectId).orElseThrow(()->
                new ResourceNotFoundException("Project not found"));
        JobTitles jobTitle = jobTitleMapper.toEntity(request);
        jobTitle.setProject(projects);
        jobTitlesRepository.save(jobTitle);
    }
    public Page<JobTitlesInfoProjection> getJobTitles(UUID projectId, String search, Pageable pageable){
        return jobTitlesRepository.findAllJobTitles(projectId, search, pageable);
    }
    public void updateJobTitle(Long id, UpdateJobTitleDto request){
        JobTitles jobTitle = jobTitlesRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Job title not found"));
        jobTitleMapper.updateJobTitle(request, jobTitle);
        jobTitlesRepository.save(jobTitle);
    }
    public void deleteJobTitle(Long id){
        JobTitles jobTitle = jobTitlesRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Job title not found"));
        jobTitlesRepository.delete(jobTitle);
    }

}
