package net.ravik_cms.ravik_backend.materials.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.materials.dto.MaterialAtRiskDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialRequired;
import net.ravik_cms.ravik_backend.materials.repository.MaterialDeliveredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialRequiredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialUsedRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialAlertService;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default {@link MaterialAlertService} implementation.
 * <p>
 * For every not-yet-completed milestone due within the alert window, checks each of its material
 * requirements against what's currently in store project-wide (delivered minus used) and flags
 * the ones falling short — the "material almost due" alert the spec calls for.
 */
@Service
@RequiredArgsConstructor
public class MaterialAlertServiceImpl implements MaterialAlertService {
    private final MilestonesRepository milestonesRepository;
    private final MaterialRequiredRepository materialRequiredRepository;
    private final MaterialDeliveredRepository materialDeliveredRepository;
    private final MaterialUsedRepository materialUsedRepository;
    private final AuthorizationService authorizationService;

    @Override
    public List<MaterialAtRiskDto> getAtRiskMaterials(UUID projectId, int daysThreshold) {
        authorizationService.authorize("READ_MATERIAL_ALERTS");

        LocalDate today = LocalDate.now();
        LocalDate thresholdDate = today.plusDays(daysThreshold);
        List<Milestones> almostDueMilestones = milestonesRepository.findAlmostDueMilestones(projectId, today, thresholdDate);

        return almostDueMilestones.stream()
                .flatMap(milestone -> atRiskMaterialsForMilestone(projectId, milestone).stream())
                .collect(Collectors.toList());
    }

    private List<MaterialAtRiskDto> atRiskMaterialsForMilestone(UUID projectId, Milestones milestone) {
        List<MaterialRequired> requirements = materialRequiredRepository.findAllByProjectIdAndMilestoneId(projectId, milestone.getId());

        return requirements.stream()
                .map(requirement -> toAtRiskDto(projectId, milestone, requirement))
                .filter(dto -> dto.shortfallQuantity() > 0)
                .collect(Collectors.toList());
    }

    private MaterialAtRiskDto toAtRiskDto(UUID projectId, Milestones milestone, MaterialRequired requirement) {
        UUID materialListId = requirement.getMaterialList().getId();
        Double totalDelivered = materialDeliveredRepository.sumDeliveredQuantity(projectId, materialListId);
        Double totalUsed = materialUsedRepository.sumUsedQuantity(projectId, materialListId);
        double inStore = totalDelivered - totalUsed;
        double shortfall = Math.max(0, requirement.getQuantityRequired() - inStore);

        return MaterialAtRiskDto.builder()
                .milestoneId(milestone.getId())
                .milestoneTitle(milestone.getTitle())
                .milestoneDueDate(milestone.getEarliestFinish())
                .materialListId(materialListId)
                .materialName(requirement.getMaterialList().getName())
                .materialMetric(requirement.getMaterialList().getMetric())
                .requiredQuantity(requirement.getQuantityRequired())
                .inStoreQuantity(inStore)
                .shortfallQuantity(shortfall)
                .build();
    }
}
