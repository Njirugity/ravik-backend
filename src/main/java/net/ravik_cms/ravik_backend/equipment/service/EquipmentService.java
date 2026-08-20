package net.ravik_cms.ravik_backend.equipment.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.equipment.dtos.UpdateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.dtos.CreateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.dtos.EquipmentInfoProjection;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import net.ravik_cms.ravik_backend.equipment.mapper.EquipmentMapper;
import net.ravik_cms.ravik_backend.equipment.repository.EquipmentRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final ProjectsRepository projectsRepository;
    private final EquipmentMapper equipmentMapper;

    public void addEquipment(CreateEquipmentDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        Equipments equipments = equipmentMapper.toEntity(request);
        equipments.setProject(project);
        equipmentRepository.save(equipments);
    }

    public Page<EquipmentInfoProjection> getEquipment(UUID projectId, String search, EquipmentCategory category, Pageable pageable) {
        return equipmentRepository.findAllEquipment(projectId, search, category, pageable);
    }

    public void updateEquipment(Long id, UpdateEquipmentDto request) {
        Equipments equipments = equipmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment not found"));
        equipmentMapper.updateEquipment(request, equipments);
        equipmentRepository.save(equipments);
    }

    public void deleteEquipment(Long id) {
        Equipments equipments = equipmentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment not found"));
        equipmentRepository.delete(equipments);
    }
}
