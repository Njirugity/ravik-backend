package net.ravik_cms.ravik_backend.materials.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import net.ravik_cms.ravik_backend.materials.entity.MaterialRequired;
import net.ravik_cms.ravik_backend.materials.mapper.MaterialRequiredMapper;
import net.ravik_cms.ravik_backend.materials.repository.MaterialListRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialRequiredRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialRequiredService;
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
 * Default {@link MaterialRequiredService} implementation.
 * <p>
 * Owns the per-milestone material baseline (quantity + unit price) that material_delivered/used
 * are compared against to compute overspend/underspend and the milestone's planned material cost.
 */
@Service
@RequiredArgsConstructor
public class MaterialRequiredServiceImpl implements MaterialRequiredService {
    private final MaterialRequiredRepository materialRequiredRepository;
    private final MaterialListRepository materialListRepository;
    private final MilestonesRepository milestonesRepository;
    private final UserRepository userRepository;
    private final MaterialRequiredMapper materialRequiredMapper;
    private final AuthorizationService authorizationService;

    /**
     * Creates a material requirement for a milestone. The project is derived from the milestone
     * (not taken from the request) so the requirement can never point at a milestone outside the
     * project it's being created under.
     *
     * @throws ResourceNotFoundException if the milestone or material type doesn't exist on the project
     */
    @Override
    @Transactional
    public MaterialRequiredDto create(UUID projectId, CreateMaterialRequiredDto dto) {
        authorizationService.authorize("CREATE_MATERIAL_REQUIRED");
        Milestones milestone = findMilestone(projectId, dto.milestoneId());
        MaterialList materialList = findMaterialList(projectId, dto.materialListId());

        MaterialRequired materialRequired = new MaterialRequired();
        materialRequired.setMilestone(milestone);
        materialRequired.setMaterialList(materialList);
        materialRequired.setProject(milestone.getProject());
        materialRequired.setQuantityRequired(dto.quantityRequired());
        materialRequired.setUnitPrice(dto.unitPrice());

        return withCreatorName(materialRequiredMapper.toDto(materialRequiredRepository.save(materialRequired)));
    }

    /**
     * @throws ResourceNotFoundException if no material requirement with that id exists on the project
     */
    @Override
    public MaterialRequiredDto get(UUID projectId, UUID id) {
        authorizationService.authorize("READ_MATERIAL_REQUIRED");
        return withCreatorName(materialRequiredMapper.toDto(findMaterialRequired(projectId, id)));
    }

    /**
     * Lists every material requirement for a milestone — the "resources" tab on the milestone page.
     */
    @Override
    public List<MaterialRequiredDto> getAllByMilestone(UUID projectId, UUID milestoneId) {
        authorizationService.authorize("READ_MATERIAL_REQUIRED");
        return withCreatorNames(materialRequiredMapper.toDtoList(
                materialRequiredRepository.findAllByProjectIdAndMilestoneId(projectId, milestoneId)));
    }

    /**
     * Lists every material requirement across the project — feeds the materials page aggregate view.
     */
    @Override
    public List<MaterialRequiredDto> getAllByProject(UUID projectId) {
        authorizationService.authorize("READ_MATERIAL_REQUIRED");
        return withCreatorNames(materialRequiredMapper.toDtoList(materialRequiredRepository.findAllByProjectId(projectId)));
    }

    /**
     * Partially updates quantity/unit price; {@code null} fields on {@code dto} are left untouched.
     *
     * @throws ResourceNotFoundException if no material requirement with that id exists on the project
     */
    @Override
    @Transactional
    public MaterialRequiredDto update(UUID projectId, UUID id, UpdateMaterialRequiredDto dto) {
        authorizationService.authorize("UPDATE_MATERIAL_REQUIRED");
        MaterialRequired materialRequired = findMaterialRequired(projectId, id);
        materialRequiredMapper.updateFromDto(dto, materialRequired);
        return withCreatorName(materialRequiredMapper.toDto(materialRequiredRepository.save(materialRequired)));
    }

    /**
     * Hard-deletes a material requirement. Safe to hard-delete since nothing else FKs to
     * material_required — unlike material_list, it isn't a referenced baseline for other rows.
     *
     * @throws ResourceNotFoundException if no material requirement with that id exists on the project
     */
    @Override
    @Transactional
    public void delete(UUID projectId, UUID id) {
        authorizationService.authorize("DELETE_MATERIAL_REQUIRED");
        materialRequiredRepository.delete(findMaterialRequired(projectId, id));
    }

    /**
     * Sums quantity_required * unit_price across a milestone's material requirements — the
     * milestone's planned material budget.
     * TODO: If this scales we may need to add fact tables on create for quick look ups
     */
    @Override
    public Double computeTotalPlannedCostForMilestone(UUID projectId, UUID milestoneId) {
        authorizationService.authorize("READ_MATERIAL_REQUIRED");
        return materialRequiredRepository.sumPlannedCostByMilestone(projectId, milestoneId);
    }

    /**
     * Sums quantity_required * unit_price across every milestone in the project — the project's
     * total planned material cost.
     * TODO: If this scales we may need to add fact tables on create for quick look ups
     */
    @Override
    public Double computeTotalPlannedCostForProject(UUID projectId) {
        authorizationService.authorize("READ_MATERIAL_REQUIRED");
        return materialRequiredRepository.sumPlannedCostByProject(projectId);
    }

    private MaterialRequired findMaterialRequired(UUID projectId, UUID id) {
        return materialRequiredRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Material requirement not found"));
    }

    private Milestones findMilestone(UUID projectId, UUID milestoneId) {
        return milestonesRepository.findByIdAndProjectId(milestoneId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));
    }

    private MaterialList findMaterialList(UUID projectId, UUID materialListId) {
        return materialListRepository.findByIdAndProjectIdAndStatus(materialListId, projectId, DeletionStatus.NOT_DELETED)
                .orElseThrow(() -> new ResourceNotFoundException("Material not found"));
    }

    private MaterialRequiredDto withCreatorName(MaterialRequiredDto dto) {
        if (dto.createdByUserId() == null) {
            return dto;
        }
        String userName = userRepository.findById(dto.createdByUserId())
                .map(Users::getUserName)
                .orElse(null);
        return dto.toBuilder().createdByUserName(userName).build();
    }

    private List<MaterialRequiredDto> withCreatorNames(List<MaterialRequiredDto> dtos) {
        List<UUID> creatorIds = dtos.stream()
                .map(MaterialRequiredDto::createdByUserId)
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
