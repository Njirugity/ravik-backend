package net.ravik_cms.ravik_backend.materials.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialStockDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedCreationResult;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialDelivered;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import net.ravik_cms.ravik_backend.materials.entity.MaterialUsed;
import net.ravik_cms.ravik_backend.materials.mapper.MaterialUsedMapper;
import net.ravik_cms.ravik_backend.materials.repository.MaterialDeliveredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialListRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialUsedRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialUsedService;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.users.UserRepository;
import net.ravik_cms.ravik_backend.users.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link MaterialUsedService} implementation.
 * <p>
 * Tracks materials actually consumed on site for a milestone, deducting from delivered stock.
 * The stock check on create is a soft warning (per product decision) rather than a hard block —
 * site record-keeping is often behind reality, so we still save the usage and flag the overdraw.
 */
@Service
@RequiredArgsConstructor
public class MaterialUsedServiceImpl implements MaterialUsedService {
    private final MaterialUsedRepository materialUsedRepository;
    private final MaterialDeliveredRepository materialDeliveredRepository;
    private final MaterialListRepository materialListRepository;
    private final MilestonesRepository milestonesRepository;
    private final UserRepository userRepository;
    private final MaterialUsedMapper materialUsedMapper;
    private final AuthorizationService authorizationService;

    /**
     * Records material usage against a milestone, deducting from the referenced delivery's
     * material stock. Returns whether this entry drives total used past total delivered for
     * that material type — a warning, not a rejection.
     *
     * @throws ResourceNotFoundException if the milestone or delivery doesn't exist on the project
     */
    @Override
    @Transactional
    public MaterialUsedCreationResult create(UUID projectId, CreateMaterialUsedDto dto) {
        authorizationService.authorize("CREATE_MATERIAL_USED");
        Milestones milestone = findMilestone(projectId, dto.milestoneId());
        MaterialDelivered materialDelivered = findMaterialDelivered(projectId, dto.materialDeliveredId());

        MaterialUsed materialUsed = new MaterialUsed();
        materialUsed.setMaterialDelivered(materialDelivered);
        materialUsed.setMilestone(milestone);
        materialUsed.setProject(milestone.getProject());
        materialUsed.setQuantityUsed(dto.quantityUsed());
        materialUsed.setDateUsed(dto.dateUsed());
        materialUsedRepository.save(materialUsed);

        UUID materialListId = materialDelivered.getMaterialList().getId();
        Double totalDelivered = materialDeliveredRepository.sumDeliveredQuantity(projectId, materialListId);
        Double totalUsed = materialUsedRepository.sumUsedQuantity(projectId, materialListId);
        Double remainingStock = totalDelivered - totalUsed;

        return MaterialUsedCreationResult.builder()
                .materialUsed(withCreatorName(materialUsedMapper.toDto(materialUsed)))
                .stockExceeded(totalUsed > totalDelivered)
                .remainingStock(remainingStock)
                .build();
    }

    /**
     * @throws ResourceNotFoundException if no usage record with that id exists on the project
     */
    @Override
    public MaterialUsedDto get(UUID projectId, UUID id) {
        authorizationService.authorize("READ_MATERIAL_USED");
        return withCreatorName(materialUsedMapper.toDto(findMaterialUsed(projectId, id)));
    }

    /**
     * Lists every usage record for a milestone — the "resources" tab on the milestone page.
     */
    @Override
    public List<MaterialUsedDto> getAllByMilestone(UUID projectId, UUID milestoneId) {
        authorizationService.authorize("READ_MATERIAL_USED");
        return withCreatorNames(materialUsedMapper.toDtoList(
                materialUsedRepository.findAllByProjectIdAndMilestoneId(projectId, milestoneId)));
    }

    /**
     * Lists every usage record across the project — feeds the materials page's used column.
     */
    @Override
    public List<MaterialUsedDto> getAllByProject(UUID projectId) {
        authorizationService.authorize("READ_MATERIAL_USED");
        return withCreatorNames(materialUsedMapper.toDtoList(materialUsedRepository.findAllByProjectId(projectId)));
    }

    /**
     * Partially updates a usage record; {@code null} fields on {@code dto} are left untouched.
     * Does not re-run the stock warning check — that's a creation-time signal, not a standing flag.
     *
     * @throws ResourceNotFoundException if no usage record with that id exists on the project
     */
    @Override
    @Transactional
    public MaterialUsedDto update(UUID projectId, UUID id, UpdateMaterialUsedDto dto) {
        authorizationService.authorize("UPDATE_MATERIAL_USED");
        MaterialUsed materialUsed = findMaterialUsed(projectId, id);
        materialUsedMapper.updateFromDto(dto, materialUsed);
        return withCreatorName(materialUsedMapper.toDto(materialUsedRepository.save(materialUsed)));
    }

    /**
     * Hard-deletes a usage record. Safe to hard-delete since nothing else FKs to material_used —
     * it's the terminal record in the material lifecycle.
     *
     * @throws ResourceNotFoundException if no usage record with that id exists on the project
     */
    @Override
    @Transactional
    public void delete(UUID projectId, UUID id) {
        authorizationService.authorize("DELETE_MATERIAL_USED");
        materialUsedRepository.delete(findMaterialUsed(projectId, id));
    }

    /**
     * Delivered vs used vs remaining for a material type — the "quantity in store" view.
     *
     * @throws ResourceNotFoundException if the material type doesn't exist on the project
     */
    @Override
    public MaterialStockDto computeStock(UUID projectId, UUID materialListId) {
        authorizationService.authorize("READ_MATERIAL_USED");
        MaterialList materialList = materialListRepository.findByIdAndProjectIdAndStatus(materialListId, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
        Double totalDelivered = materialDeliveredRepository.sumDeliveredQuantity(projectId, materialListId);
        Double totalUsed = materialUsedRepository.sumUsedQuantity(projectId, materialListId);
        return MaterialStockDto.builder()
                .materialListId(materialListId)
                .materialName(materialList.getName())
                .materialMetric(materialList.getMetric())
                .totalDelivered(totalDelivered)
                .totalUsed(totalUsed)
                .remaining(totalDelivered - totalUsed)
                .build();
    }

    private MaterialUsed findMaterialUsed(UUID projectId, UUID id) {
        return materialUsedRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Material usage record not found"));
    }

    private Milestones findMilestone(UUID projectId, UUID milestoneId) {
        return milestonesRepository.findByIdAndProjectId(milestoneId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));
    }

    private MaterialDelivered findMaterialDelivered(UUID projectId, UUID materialDeliveredId) {
        return materialDeliveredRepository.findByIdAndProjectIdAndStatus(materialDeliveredId, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material delivery not found"));
    }

    private MaterialUsedDto withCreatorName(MaterialUsedDto dto) {
        if (dto.createdByUserId() == null) {
            return dto;
        }
        String userName = userRepository.findById(dto.createdByUserId())
                .map(Users::getUserName)
                .orElse(null);
        return dto.toBuilder().createdByUserName(userName).build();
    }

    private List<MaterialUsedDto> withCreatorNames(List<MaterialUsedDto> dtos) {
        List<UUID> creatorIds = dtos.stream()
                .map(MaterialUsedDto::createdByUserId)
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
