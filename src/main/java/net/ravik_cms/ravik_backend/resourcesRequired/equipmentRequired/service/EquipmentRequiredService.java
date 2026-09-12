package net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import net.ravik_cms.ravik_backend.equipment.repository.EquipmentRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.CreateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.EquipmentRequiredInfoProjection;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.UpdateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.mapper.EquipmentRequiredMapper;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.repository.EquipmentRequiredRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentRequiredService {
    private final EquipmentRequiredRepository equipmentRequiredRepository;
    private final MilestonesRepository milestonesRepository;
    private final EquipmentRepository equipmentRepository;
    private final EquipmentRequiredMapper equipmentRequiredMapper;

    @Transactional
    public List<EquipmentRequiredInfoProjection> createEquipmentRequired(UUID milestoneId, List<CreateEquipmentRequiredDto> requests) {
        Milestones milestone = milestonesRepository.findById(milestoneId).orElseThrow(() ->
                new ResourceNotFoundException("Milestone not found"));

        List<EquipmentRequired> entries = requests.stream()
                .map(dto -> {
                    Equipments equipment = equipmentRepository.findById(dto.getEquipmentId()).orElseThrow(() ->
                            new ResourceNotFoundException("Equipment not found"));
                    EquipmentRequired entry = equipmentRequiredMapper.toEntity(dto);
                    entry.setEquipments(equipment);
                    entry.setMilestone(milestone);
                    entry.setProject(milestone.getProject());
                    return entry;
                }).toList();

        equipmentRequiredRepository.saveAll(entries);
        return getEquipmentRequired(milestoneId);
    }

    public List<EquipmentRequiredInfoProjection> getEquipmentRequired(UUID milestoneId) {
        return equipmentRequiredRepository.findAllByMilestoneId(milestoneId);
    }
    public Page<EquipmentRequiredInfoProjection> getEquipmentRequiredBYProject(
            UUID projectId, String search, EquipmentCategory category, Pageable pageable){
        return equipmentRequiredRepository.findAllByProjectId(projectId, search, category, pageable);
    }

    public void updateEquipmentRequired(Long id, UpdateEquipmentRequiredDto request) {
        EquipmentRequired entry = equipmentRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment required entry not found"));
        if (request.getEquipmentId() != null) {
            Equipments equipment = equipmentRepository.findById(request.getEquipmentId()).orElseThrow(() ->
                    new ResourceNotFoundException("Equipment not found"));
            entry.setEquipments(equipment);
        }
        equipmentRequiredMapper.updateEquipmentRequired(request, entry);
        equipmentRequiredRepository.save(entry);
    }

    public void deleteEquipmentRequired(Long id) {
        EquipmentRequired entry = equipmentRequiredRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment required entry not found"));
        equipmentRequiredRepository.delete(entry);
    }
}
