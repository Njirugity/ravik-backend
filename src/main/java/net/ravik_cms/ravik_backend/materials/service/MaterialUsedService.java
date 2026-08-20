package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialStockDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedCreationResult;
import net.ravik_cms.ravik_backend.materials.dto.MaterialUsedDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialUsedDto;

import java.util.List;
import java.util.UUID;

public interface MaterialUsedService {
    MaterialUsedCreationResult create(UUID projectId, CreateMaterialUsedDto dto);
    MaterialUsedDto get(UUID projectId, UUID id);
    List<MaterialUsedDto> getAllByMilestone(UUID projectId, UUID milestoneId);
    List<MaterialUsedDto> getAllByProject(UUID projectId);
    MaterialUsedDto update(UUID projectId, UUID id, UpdateMaterialUsedDto dto);
    void delete(UUID projectId, UUID id);
    MaterialStockDto computeStock(UUID projectId, UUID materialListId);
}
