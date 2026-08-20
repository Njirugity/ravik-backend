package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.MaterialAtRiskDto;

import java.util.List;
import java.util.UUID;

public interface MaterialAlertService {
    List<MaterialAtRiskDto> getAtRiskMaterials(UUID projectId, int daysThreshold);
}
