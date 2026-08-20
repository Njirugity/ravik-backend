package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialRequiredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialRequiredDto;

import java.util.List;
import java.util.UUID;

public interface MaterialRequiredService {
    MaterialRequiredDto create(UUID projectId, CreateMaterialRequiredDto dto);
    MaterialRequiredDto get(UUID projectId, UUID id);
    List<MaterialRequiredDto> getAllByMilestone(UUID projectId, UUID milestoneId);
    List<MaterialRequiredDto> getAllByProject(UUID projectId);
    MaterialRequiredDto update(UUID projectId, UUID id, UpdateMaterialRequiredDto dto);
    void delete(UUID projectId, UUID id);
    Double computeTotalPlannedCostForMilestone(UUID projectId, UUID milestoneId);
    Double computeTotalPlannedCostForProject(UUID projectId);
}
