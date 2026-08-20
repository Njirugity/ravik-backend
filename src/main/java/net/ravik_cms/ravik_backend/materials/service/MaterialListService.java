package net.ravik_cms.ravik_backend.materials.service;

import net.ravik_cms.ravik_backend.materials.dto.CreateMaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.MaterialListDto;
import net.ravik_cms.ravik_backend.materials.dto.UpdateMaterialListDto;

import java.util.List;
import java.util.UUID;

public interface MaterialListService {
    MaterialListDto create(UUID projectId, CreateMaterialListDto dto);
    MaterialListDto get(UUID projectId, UUID id);
    List<MaterialListDto> getAll(UUID projectId);
    MaterialListDto update(UUID projectId, UUID id, UpdateMaterialListDto dto);
    void delete(UUID projectId, UUID id);
}
