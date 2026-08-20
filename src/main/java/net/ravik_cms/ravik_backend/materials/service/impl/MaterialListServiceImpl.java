package net.ravik_cms.ravik_backend.materials.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import net.ravik_cms.ravik_backend.materials.mapper.MaterialListMapper;
import net.ravik_cms.ravik_backend.materials.repository.MaterialListRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialListService;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Default {@link MaterialListService} implementation.
 * <p>
 * Backs the project-scoped catalogue of material types (name + metric) that
 * {@code material_required}, {@code material_delivered} and {@code material_used}
 * records point to, so material input stays consistent within a project.
 */
@Service
@RequiredArgsConstructor
public class MaterialListServiceImpl implements MaterialListService {
    private final MaterialListRepository materialListRepository;
    private final ProjectsRepository projectsRepository;
    private final MaterialListMapper materialListMapper;
    private final AuthorizationService authorizationService;

    /**
     * Creates a new material type under the given project.
     *
     * @param projectId project the material type belongs to
     * @param dto       name and metric for the new material type
     * @return the created material type
     * @throws ResourceNotFoundException if the project does not exist
     */
    @Override
    @Transactional
    public MaterialListDto create(UUID projectId, CreateMaterialListDto dto) {
        authorizationService.authorize("CREATE_MATERIAL_LIST");
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        MaterialList materialList = materialListMapper.fromCreateDto(dto);
        materialList.setProject(project);
        return materialListMapper.toDto(materialListRepository.save(materialList));
    }

    /**
     * Fetches a single material type, scoped to the given project.
     *
     * @throws ResourceNotFoundException if no material type with that id exists on the project
     */
    @Override
    public MaterialListDto get(UUID projectId, UUID id) {
        authorizationService.authorize("READ_MATERIAL_LIST");
        return materialListMapper.toDto(findMaterial(projectId, id));
    }

    /**
     * Lists every material type defined for the given project.
     */
    @Override
    public List<MaterialListDto> getAll(UUID projectId) {
        authorizationService.authorize("READ_MATERIAL_LIST");
        return materialListMapper.toDtoList(materialListRepository.findAllByProjectIdAndStatus(projectId, DeletionStatus.NOT_DELETED));
    }

    /**
     * Partially updates a material type; {@code null} fields on {@code dto} are left untouched.
     *
     * @throws ResourceNotFoundException if no material type with that id exists on the project
     */
    @Override
    @Transactional
    public MaterialListDto update(UUID projectId, UUID id, UpdateMaterialListDto dto) {
        authorizationService.authorize("UPDATE_MATERIAL_LIST");
        MaterialList materialList = findMaterial(projectId, id);
        materialListMapper.updateFromDto(dto, materialList);
        return materialListMapper.toDto(materialListRepository.save(materialList));
    }

    /**
     * Soft-deletes a material type from the project. The row is kept (flagged
     * {@link DeletionStatus#DELETED}) rather than removed, since material_required/delivered/used
     * rows may already reference it and a hard delete would either violate the FK or orphan
     * historical records.
     *
     * @throws ResourceNotFoundException if no material type with that id exists on the project
     */
    @Override
    @Transactional
    public void delete(UUID projectId, UUID id) {
        authorizationService.authorize("DELETE_MATERIAL_LIST");
        MaterialList materialList = findMaterial(projectId, id);
        materialList.setStatus(DeletionStatus.DELETED);
        materialListRepository.save(materialList);
    }

    private MaterialList findMaterial(UUID projectId, UUID id) {
        return materialListRepository.findByIdAndProjectIdAndStatus(id, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
    }
}
