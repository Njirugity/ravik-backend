package net.ravik_cms.ravik_backend.labourPayout.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.utils.ReferenceCodeGenerator;
import net.ravik_cms.ravik_backend.labourPayout.dtos.CreateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.dtos.LabourPayoutInfoProjection;
import net.ravik_cms.ravik_backend.labourPayout.dtos.UpdateLabourPayoutDto;
import net.ravik_cms.ravik_backend.labourPayout.entity.LabourPayout;
import net.ravik_cms.ravik_backend.labourPayout.mapper.LabourPayoutMapper;
import net.ravik_cms.ravik_backend.labourPayout.repository.LabourPayoutRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.wages.Wages;
import net.ravik_cms.ravik_backend.wages.WagesRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LabourPayoutService {
    private final LabourPayoutRepository labourPayoutRepository;
    private final WagesRepository wagesRepository;
    private final LabourPayoutMapper labourPayoutMapper;
    private final ProjectsRepository projectsRepository;

    public void addLabourPayout(CreateLabourPayoutDto request, UUID projectId) {
        LabourPayout payout = labourPayoutMapper.toEntity(request);
        payout.setProjectId(projectId);
        payout.setPaymentStatus(PaymentStatus.PENDING);
        payout.setReferenceCode(ReferenceCodeGenerator.generate("LB", labourPayoutRepository::existsByReferenceCode));
        labourPayoutRepository.save(payout);
    }

    @Transactional
    public void createPayoutAndLink(UUID projectId, List<Wages> records, LocalDate start, LocalDate end) {
        Projects p = projectsRepository.findById(projectId)
                .orElseThrow(()->new ResourceNotFoundException("Project not found"));
        double totalAmount = records.stream().mapToDouble(Wages::getGrossPay).sum();

        LabourPayout payout = new LabourPayout();
        payout.setProjectId(projectId);
        payout.setTotalAmount(totalAmount);
        payout.setPaymentStatus(PaymentStatus.PENDING);
        payout.setReferenceCode(ReferenceCodeGenerator.generate("LB", labourPayoutRepository::existsByReferenceCode));
        payout.setPeriodStart(start);
        payout.setPeriodEnd(end);
        payout = labourPayoutRepository.save(payout);

        LabourPayout finalPayout = payout;
        records.forEach(w -> w.setLabourPayout(finalPayout));
        wagesRepository.saveAll(records);
    }

    public Page<LabourPayoutInfoProjection> getLabourPayouts(UUID projectId, PaymentStatus paymentStatus, Pageable pageable) {
        return labourPayoutRepository.findAllByProjectId(projectId, paymentStatus, pageable);
    }

    public void updateLabourPayout(UUID id, UpdateLabourPayoutDto request) {
        LabourPayout payout = labourPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Labour payout not found"));
        labourPayoutMapper.updateLabourPayout(request, payout);
        labourPayoutRepository.save(payout);
    }

    public void deleteLabourPayout(UUID id) {
        LabourPayout payout = labourPayoutRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Labour payout not found"));
        labourPayoutRepository.delete(payout);
    }
}
