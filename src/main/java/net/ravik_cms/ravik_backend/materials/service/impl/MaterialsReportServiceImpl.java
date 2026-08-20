package net.ravik_cms.ravik_backend.materials.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.authorization.AuthorizationService;
import net.ravik_cms.ravik_backend.common.enums.DeletionStatus;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredAggregateProjection;
import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredAggregateProjection;
import net.ravik_cms.ravik_backend.materials.dto.MaterialSummaryDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedAggregateProjection;
import net.ravik_cms.ravik_backend.materials.dto.MaterialsReportDto;
import net.ravik_cms.ravik_backend.materials.entity.MaterialList;
import net.ravik_cms.ravik_backend.materials.repository.MaterialDeliveredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialListRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialRequiredRepository;
import net.ravik_cms.ravik_backend.materials.repository.MaterialUsedRepository;
import net.ravik_cms.ravik_backend.materials.service.MaterialsReportService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default {@link MaterialsReportService} implementation.
 * <p>
 * Builds the materials page: every material type in the project with its planned (required),
 * actual (delivered/used) figures, and the variances that surface over/underspend and
 * over/underuse — aggregated across every milestone in one pass rather than per-milestone.
 */
@Service
@RequiredArgsConstructor
public class MaterialsReportServiceImpl implements MaterialsReportService {
    private final MaterialListRepository materialListRepository;
    private final MaterialRequiredRepository materialRequiredRepository;
    private final MaterialDeliveredRepository materialDeliveredRepository;
    private final MaterialUsedRepository materialUsedRepository;
    private final AuthorizationService authorizationService;

    @Override
    public MaterialsReportDto getProjectReport(UUID projectId) {
        authorizationService.authorize("READ_MATERIALS_REPORT");

        List<MaterialList> materialTypes = materialListRepository.findAllByProjectIdAndStatus(projectId, DeletionStatus.NOT_DELETED);

        Map<UUID, MaterialRequiredAggregateProjection> requiredByMaterial = materialRequiredRepository
                .aggregateByMaterialForProject(projectId).stream()
                .collect(Collectors.toMap(MaterialRequiredAggregateProjection::materialListId, Function.identity()));
        Map<UUID, MaterialDeliveredAggregateProjection> deliveredByMaterial = materialDeliveredRepository
                .aggregateByMaterialForProject(projectId).stream()
                .collect(Collectors.toMap(MaterialDeliveredAggregateProjection::materialListId, Function.identity()));
        Map<UUID, MaterialUsedAggregateProjection> usedByMaterial = materialUsedRepository
                .aggregateByMaterialForProject(projectId).stream()
                .collect(Collectors.toMap(MaterialUsedAggregateProjection::materialListId, Function.identity()));

        List<MaterialSummaryDto> rows = materialTypes.stream()
                .map(materialList -> buildRow(materialList, requiredByMaterial, deliveredByMaterial, usedByMaterial))
                .collect(Collectors.toList());

        double totalRequiredCost = rows.stream().mapToDouble(MaterialSummaryDto::requiredCost).sum();
        double totalDeliveredCost = rows.stream().mapToDouble(MaterialSummaryDto::deliveredCost).sum();

        return MaterialsReportDto.builder()
                .materials(rows)
                .totalRequiredCost(totalRequiredCost)
                .totalDeliveredCost(totalDeliveredCost)
                .totalCostVariance(totalDeliveredCost - totalRequiredCost)
                .build();
    }

    private MaterialSummaryDto buildRow(
            MaterialList materialList,
            Map<UUID, MaterialRequiredAggregateProjection> requiredByMaterial,
            Map<UUID, MaterialDeliveredAggregateProjection> deliveredByMaterial,
            Map<UUID, MaterialUsedAggregateProjection> usedByMaterial
    ) {
        UUID materialListId = materialList.getId();
        MaterialRequiredAggregateProjection required = requiredByMaterial.get(materialListId);
        MaterialDeliveredAggregateProjection delivered = deliveredByMaterial.get(materialListId);
        MaterialUsedAggregateProjection used = usedByMaterial.get(materialListId);

        double requiredQuantity = required != null && required.totalQuantityRequired() != null ? required.totalQuantityRequired() : 0;
        double requiredCost = required != null && required.totalCostRequired() != null ? required.totalCostRequired() : 0;
        double deliveredQuantity = delivered != null && delivered.totalQuantityDelivered() != null ? delivered.totalQuantityDelivered() : 0;
        double deliveredCost = delivered != null && delivered.totalCostDelivered() != null ? delivered.totalCostDelivered() : 0;
        double usedQuantity = used != null && used.totalQuantityUsed() != null ? used.totalQuantityUsed() : 0;

        return MaterialSummaryDto.builder()
                .materialListId(materialListId)
                .materialName(materialList.getName())
                .materialMetric(materialList.getMetric())
                .requiredQuantity(requiredQuantity)
                .requiredCost(requiredCost)
                .deliveredQuantity(deliveredQuantity)
                .deliveredCost(deliveredCost)
                .usedQuantity(usedQuantity)
                .balance(deliveredQuantity - usedQuantity)
                .quantityVariance(usedQuantity - requiredQuantity)
                .costVariance(deliveredCost - requiredCost)
                .build();
    }
}
