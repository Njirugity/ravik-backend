package net.ravik_cms.ravik_backend.projects;

import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ProjectService {
    private final ProjectsRepository projectsRepository;
    private final ProjectMapper projectMapper;

    public ProjectService(ProjectsRepository projectsRepository, ProjectMapper projectMapper) {
        this.projectsRepository = projectsRepository;
        this.projectMapper = projectMapper;
    }

    public ProjectInfoDto createProject(ProjectDto projectDto){
        Projects newProject = projectMapper.toProjects(projectDto);
        Projects savedProject = projectsRepository.save(newProject);
        return projectMapper.toInfoDto(savedProject);
    }

    public ProjectInfoDto getProject(UUID id){
        Projects project = projectsRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Project not found"));
        return projectMapper.toInfoDto(project);

    }

    public List<ProjectInfoDto> getAllProjects(){
        List<Projects> projects = projectsRepository.findAll();
        return projectMapper.toInfoDtoList(projects);
    }

    public ProjectPatchDto patchProject(UUID id, ProjectPatchDto request){
        Projects project = projectsRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Project not found"));
        if(request.getTitle() != null){
            project.setTitle(request.getTitle());
        }
        if(request.getLocation() != null){
            project.setLocation(request.getLocation());
        }
        if(request.getPlotNo() != null){
            project.setPlotNo(request.getPlotNo());
        }
        if(request.getAddress() != null){
            project.setAddress(request.getAddress());
        }
        if(request.getNCAregNumber()!= null){
            project.setNCAregNumber(request.getNCAregNumber());
        }
        if(request.getNEMAregNumber() != null){
            project.setNEMAregNumber(request.getNEMAregNumber());
        }
        if(request.getCountyRegNumber() != null){
            project.setCountyRegNumber(request.getCountyRegNumber());
        }
        Projects savedProject = projectsRepository.save(project);
        return projectMapper.toProjectPatchDto(savedProject);
    }

    public Void deleteProject(UUID id){
        Projects project = projectsRepository.findById(id).
                orElseThrow(()->new ResourceNotFoundException("Project not found"));
        projectsRepository.delete(project);
        return null;
    }
}
