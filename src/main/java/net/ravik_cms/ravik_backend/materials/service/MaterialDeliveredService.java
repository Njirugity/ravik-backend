package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialDeliveredDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialDeliveredDto;

import java.util.List;
import java.util.UUID;

public interface MaterialDeliveredService {
    MaterialDeliveredDto create(UUID projectId, CreateMaterialDeliveredDto dto);
    MaterialDeliveredDto get(UUID projectId, UUID id);
    List<MaterialDeliveredDto> getAllByProject(UUID projectId);
    List<MaterialDeliveredDto> getAllByMaterial(UUID projectId, UUID materialListId);
    MaterialDeliveredDto update(UUID projectId, UUID id, UpdateMaterialDeliveredDto dto);
    void delete(UUID projectId, UUID id);
    Double computeTotalDelivered(UUID projectId, UUID materialListId);
}
