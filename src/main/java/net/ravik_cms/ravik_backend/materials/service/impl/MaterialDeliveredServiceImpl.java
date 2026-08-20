package net.ravik_cms.ravik_backend.materials.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialDelivered;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import net.ravik_cms.ravik_backend.materials.mapper.MaterialDeliveredMapper;
import net.ravik_cms.ravik_backend.materials.repository.MaterialDeliveredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialListRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialDeliveredService;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link MaterialDeliveredService} implementation.
 * <p>
 * Tracks materials delivered to the site. Belongs to a project, not a milestone — bulk/site-wide
 * deliveries are common — and is soft-deleted rather than hard-deleted since material_used rows
 * (once built) will reference a delivery to deduct stock from.
 */
@Service
@RequiredArgsConstructor
public class MaterialDeliveredServiceImpl implements MaterialDeliveredService {
    private final MaterialDeliveredRepository materialDeliveredRepository;
    private final MaterialListRepository materialListRepository;
    private final UserRepository userRepository;
    private final MaterialDeliveredMapper materialDeliveredMapper;
    private final AuthorizationService authorizationService;

    /**
     * Records a delivery of a material type to the project.
     *
     * @throws ResourceNotFoundException if the material type doesn't exist on the project
     */
    @Override
    @Transactional
    public MaterialDeliveredDto create(UUID projectId, CreateMaterialDeliveredDto dto) {
        authorizationService.authorize("CREATE_MATERIAL_DELIVERED");
        MaterialList materialList = findMaterialList(projectId, dto.materialListId());

        MaterialDelivered materialDelivered = new MaterialDelivered();
        materialDelivered.setMaterialList(materialList);
        materialDelivered.setProject(materialList.getProject());
        materialDelivered.setQuantityDelivered(dto.quantityDelivered());
        materialDelivered.setUnitPrice(dto.unitPrice());
        materialDelivered.setDateDelivered(dto.dateDelivered());

        return withCreatorName(materialDeliveredMapper.toDto(materialDeliveredRepository.save(materialDelivered)));
    }

    /**
     * @throws ResourceNotFoundException if no delivery with that id exists on the project
     */
    @Override
    public MaterialDeliveredDto get(UUID projectId, UUID id) {
        authorizationService.authorize("READ_MATERIAL_DELIVERED");
        return withCreatorName(materialDeliveredMapper.toDto(findMaterialDelivered(projectId, id)));
    }

    /**
     * Lists every delivery for the project — feeds the materials page's delivered column.
     */
    @Override
    public List<MaterialDeliveredDto> getAllByProject(UUID projectId) {
        authorizationService.authorize("READ_MATERIAL_DELIVERED");
        return withCreatorNames(materialDeliveredMapper.toDtoList(
                materialDeliveredRepository.findAllByProjectIdAndStatus(projectId, DeletionStatus.NOT_DELETED)));
    }

    /**
     * Lists every delivery of a specific material type — e.g. to show a delivery history/audit trail.
     */
    @Override
    public List<MaterialDeliveredDto> getAllByMaterial(UUID projectId, UUID materialListId) {
        authorizationService.authorize("READ_MATERIAL_DELIVERED");
        return withCreatorNames(materialDeliveredMapper.toDtoList(
                materialDeliveredRepository.findAllByProjectIdAndMaterialListIdAndStatus(
                        projectId, materialListId, DeletionStatus.NOT_DELETED)));
    }

    /**
     * Partially updates a delivery; {@code null} fields on {@code dto} are left untouched.
     *
     * @throws ResourceNotFoundException if no delivery with that id exists on the project
     */
    @Override
    @Transactional
    public MaterialDeliveredDto update(UUID projectId, UUID id, UpdateMaterialDeliveredDto dto) {
        authorizationService.authorize("UPDATE_MATERIAL_DELIVERED");
        MaterialDelivered materialDelivered = findMaterialDelivered(projectId, id);
        materialDeliveredMapper.updateFromDto(dto, materialDelivered);
        return withCreatorName(materialDeliveredMapper.toDto(materialDeliveredRepository.save(materialDelivered)));
    }

    /**
     * Soft-deletes a delivery. The row is kept (flagged {@link DeletionStatus#DELETED}) rather than
     * removed, since material_used rows may already reference it to deduct stock.
     *
     * @throws ResourceNotFoundException if no delivery with that id exists on the project
     */
    @Override
    @Transactional
    public void delete(UUID projectId, UUID id) {
        authorizationService.authorize("DELETE_MATERIAL_DELIVERED");
        MaterialDelivered materialDelivered = findMaterialDelivered(projectId, id);
        materialDelivered.setStatus(DeletionStatus.DELETED);
        materialDeliveredRepository.save(materialDelivered);
    }

    /**
     * Sums quantity_delivered for a material type across the project — the "in store" figure
     * material_used will deduct from.
     */
    @Override
    public Double computeTotalDelivered(UUID projectId, UUID materialListId) {
        authorizationService.authorize("READ_MATERIAL_DELIVERED");
        return materialDeliveredRepository.sumDeliveredQuantity(projectId, materialListId);
    }

    private MaterialDelivered findMaterialDelivered(UUID projectId, UUID id) {
        return materialDeliveredRepository.findByIdAndProjectIdAndStatus(id, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material delivery not found"));
    }

    private MaterialList findMaterialList(UUID projectId, UUID materialListId) {
        return materialListRepository.findByIdAndProjectIdAndStatus(materialListId, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
    }

    private MaterialDeliveredDto withCreatorName(MaterialDeliveredDto dto) {
        if (dto.createdByUserId() == null) {
            return dto;
        }
        String userName = userRepository.findById(dto.createdByUserId())
                .map(Users::getUserName)
                .orElse(null);
        return dto.toBuilder().createdByUserName(userName).build();
    }

    private List<MaterialDeliveredDto> withCreatorNames(List<MaterialDeliveredDto> dtos) {
        List<UUID> creatorIds = dtos.stream()
                .map(MaterialDeliveredDto::createdByUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<UUID, String> userNamesById = userRepository.findAllById(creatorIds).stream()
                .collect(Collectors.toMap(Users::getId, Users::getUserName));
        return dtos.stream()
                .map(dto -> dto.toBuilder().createdByUserName(userNamesById.get(dto.createdByUserId())).build())
                .collect(Collectors.toList());
    }
}
