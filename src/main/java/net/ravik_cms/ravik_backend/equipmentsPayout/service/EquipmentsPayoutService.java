package net.ravik_cms.ravik_backend.equipmentsPayout.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.utils.PaymentStatusCalculator;
import net.ravik_cms.ravik_backend.common.utils.ReferenceCodeGenerator;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.CreateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.EquipmentsPayoutInfoProjection;
import net.ravik_cms.ravik_backend.equipmentsPayout.dtos.UpdateEquipmentsPayoutDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.entity.EquipmentsPayout;
import net.ravik_cms.ravik_backend.equipmentsPayout.mapper.EquipmentsPayoutMapper;
import net.ravik_cms.ravik_backend.equipmentsPayout.repository.EquipmentsPayoutRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.entity.EquipmentRequired;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.repository.EquipmentRequiredRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentsPayoutService {
    private final EquipmentsPayoutRepository equipmentsPayoutRepository;
    private final MilestonesRepository milestonesRepository;
    private final EquipmentRequiredRepository equipmentRequiredRepository;
    private final EquipmentsPayoutMapper equipmentsPayoutMapper;

    @Transactional
    public void addEquipmentsPayout(UUID milestoneId, CreateEquipmentsPayoutDto request) {
        Milestones milestone = milestonesRepository.findById(milestoneId).orElseThrow(() ->
                new ResourceNotFoundException("Milestone not found"));
        EquipmentRequired equipmentRequired = equipmentRequiredRepository.findById(request.getEquipmentRequiredId()).orElseThrow(() ->
                new ResourceNotFoundException("Equipment required entry not found"));

        EquipmentsPayout payout = equipmentsPayoutMapper.toEntity(request);
        payout.setEquipmentRequired(equipmentRequired);
        payout.setMilestone(milestone);
        payout.setProject(milestone.getProject());
        payout.setPaymentStatus(calculatePaymentStatus(payout));
        payout.setReferenceCode(ReferenceCodeGenerator.generate("EQ", equipmentsPayoutRepository::existsByReferenceCode));
        equipmentsPayoutRepository.save(payout);
    }

    public List<EquipmentsPayoutInfoProjection> getEquipmentsPayout(UUID milestoneId) {
        return equipmentsPayoutRepository.findAllByMilestoneId(milestoneId);
    }

    public Page<EquipmentsPayoutInfoProjection> getByProject(
            UUID projectId, String search, EquipmentCategory category, LocalDate dateUsed, Pageable pageable) {
        return equipmentsPayoutRepository.findAllByProjectId(projectId, search, category, dateUsed, pageable);
    }

    public void updateEquipmentsPayout(UUID id, UpdateEquipmentsPayoutDto request) {
        EquipmentsPayout payout = equipmentsPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment payout not found"));

        equipmentsPayoutMapper.updateEquipmentsPayout(request, payout);
        payout.setPaymentStatus(calculatePaymentStatus(payout));
        equipmentsPayoutRepository.save(payout);
    }

    private PaymentStatus calculatePaymentStatus(EquipmentsPayout payout) {
        EquipmentCategory category = payout.getEquipmentRequired().getEquipments().getCategory();
        long totalCost = payout.getOperatorCost() + (category == EquipmentCategory.OWNED ? payout.getFuelCost() : payout.getRentalCost());
        return PaymentStatusCalculator.calculate(totalCost, payout.getPaidAmount());
    }

    public void deleteEquipmentsPayout(UUID id) {
        EquipmentsPayout payout = equipmentsPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Equipment payout not found"));
        equipmentsPayoutRepository.delete(payout);
    }
}
