package net.ravik_cms.ravik_backend.payment.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.utils.PaymentStatusCalculator;
import net.ravik_cms.ravik_backend.equipmentsPayout.entity.EquipmentsPayout;
import net.ravik_cms.ravik_backend.equipmentsPayout.repository.EquipmentsPayoutRepository;
import net.ravik_cms.ravik_backend.labourPayout.entity.LabourPayout;
import net.ravik_cms.ravik_backend.labourPayout.repository.LabourPayoutRepository;
import net.ravik_cms.ravik_backend.payment.dtos.CreatePaymentDto;
import net.ravik_cms.ravik_backend.payment.dtos.PaymentInfoProjection;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import net.ravik_cms.ravik_backend.payment.dtos.UpdatePaymentDto;
import net.ravik_cms.ravik_backend.payment.entity.Payment;
import net.ravik_cms.ravik_backend.payment.mapper.PaymentMapper;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.subContractorPayout.entity.SubContractorPayout;
import net.ravik_cms.ravik_backend.subContractorPayout.repository.SubContractorPayoutRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final EquipmentsPayoutRepository equipmentsPayoutRepository;
    private final SubContractorPayoutRepository subContractorPayoutRepository;
    private final LabourPayoutRepository labourPayoutRepository;
    private final ProjectsRepository projectsRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void addPayment(UUID projectId, CreatePaymentDto request) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        Payment payment = paymentMapper.toEntity(request);
        payment.setProject(project);
        if (request.getAccountId() != null) {
            Accounts account = accountRepository.findById(request.getAccountId()).orElseThrow(() ->
                    new ResourceNotFoundException("Account not found"));
            payment.setAccount(account);
        }
        applyToPayout(payment, request.getPaymentCategory(), request.getReferenceId(), request.getAmount());
        paymentRepository.save(payment);
    }

    public Page<PaymentInfoProjection> getAllPayments(
            UUID projectId, PaymentCategory category, PaymentStatus status, LocalDate datePaid, Pageable pageable) {
        return paymentRepository.findAllByProjectId(projectId, category, status, datePaid, pageable);
    }

    public Page<PayoutSummaryProjection> getAllPayouts(
            UUID projectId, PaymentCategory category, PaymentStatus status, Pageable pageable) {
        List<PayoutSummaryProjection> summaries = new ArrayList<>();
        if (category == null || category == PaymentCategory.EQUIPMENT) {
            summaries.addAll(equipmentsPayoutRepository.findPayoutSummaries(projectId, status));
        }
        if (category == null || category == PaymentCategory.SUBCONTRACTOR) {
            summaries.addAll(subContractorPayoutRepository.findPayoutSummaries(projectId, status));
        }
        if (category == null || category == PaymentCategory.LABOUR) {
            summaries.addAll(labourPayoutRepository.findPayoutSummaries(projectId, status));
        }
        summaries.sort(Comparator.comparing(PayoutSummaryProjection::date, Comparator.nullsLast(Comparator.reverseOrder())));

        int start = Math.min((int) pageable.getOffset(), summaries.size());
        int end = Math.min(start + pageable.getPageSize(), summaries.size());
        return new PageImpl<>(summaries.subList(start, end), pageable, summaries.size());
    }

    @Transactional
    public void updatePayment(UUID id, UpdatePaymentDto request) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Payment not found"));
        double previousAmount = payment.getAmount();
        paymentMapper.updatePayment(request, payment);
        if (request.getAccountId() != null) {
            Accounts account = accountRepository.findById(request.getAccountId()).orElseThrow(() ->
                    new ResourceNotFoundException("Account not found"));
            payment.setAccount(account);
        }
        double delta = payment.getAmount() - previousAmount;
        applyToPayout(payment, payment.getPaymentCategory(), payment.getReferenceId(), delta);
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(UUID id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Payment not found"));
        applyToPayout(payment, payment.getPaymentCategory(), payment.getReferenceId(), -payment.getAmount());
        paymentRepository.delete(payment);
    }

    private void applyToPayout(Payment payment, PaymentCategory category, Long referenceId, double amountDelta) {
        switch (category) {
            case EQUIPMENT -> {
                EquipmentsPayout payout = equipmentsPayoutRepository.findById(referenceId).orElseThrow(() ->
                        new ResourceNotFoundException("Equipment payout not found"));
                EquipmentCategory equipmentCategory = payout.getEquipmentRequired().getEquipments().getCategory();
                double totalCost = payout.getOperatorCost() + (equipmentCategory == EquipmentCategory.OWNED ? payout.getFuelCost() : payout.getRentalCost());
                PaymentStatus status = applyAndCalculate(payout::getPaidAmount, payout::setPaidAmount, totalCost, amountDelta);
                payout.setPaymentStatus(status);
                equipmentsPayoutRepository.save(payout);
                payment.setPaymentCategory(payout.getPaymentCategory());
                payment.setReferenceCode(payout.getReferenceCode());
                payment.setPaymentStatus(status);
                payment.setReferenceId(payout.getId());
            }
            case SUBCONTRACTOR -> {
                SubContractorPayout payout = subContractorPayoutRepository.findById(referenceId).orElseThrow(() ->
                        new ResourceNotFoundException("Sub contractor payout not found"));
                double totalCost = payout.getActualJobCost() == null ? 0d : payout.getActualJobCost();
                PaymentStatus status = applyAndCalculate(payout::getPaidAmount, payout::setPaidAmount, totalCost, amountDelta);
                payout.setPaymentStatus(status);
                subContractorPayoutRepository.save(payout);
                payment.setPaymentCategory(payout.getPaymentCategory());
                payment.setReferenceCode(payout.getReferenceCode());
                payment.setPaymentStatus(status);
                payment.setReferenceId(payout.getId());
            }
            case LABOUR -> {
                LabourPayout payout = labourPayoutRepository.findById(referenceId).orElseThrow(() ->
                        new ResourceNotFoundException("Labour payout not found"));
                double totalCost = payout.getTotalAmount() == null ? 0d : payout.getTotalAmount();
                PaymentStatus status = applyAndCalculate(payout::getPaidAmount, payout::setPaidAmount, totalCost, amountDelta);
                payout.setPaymentStatus(status);
                labourPayoutRepository.save(payout);
                payment.setPaymentCategory(payout.getPaymentCategory());
                payment.setReferenceCode(payout.getReferenceCode());
                payment.setPaymentStatus(status);
                payment.setReferenceId(payout.getId());
            }
            default -> throw new UnsupportedOperationException("Unsupported payment category: " + category);
        }
    }

    private PaymentStatus applyAndCalculate(
            java.util.function.Supplier<Double> getPaidAmount,
            java.util.function.Consumer<Double> setPaidAmount,
            double totalCost, double amountDelta) {
        double newPaidAmount = (getPaidAmount.get() == null ? 0d : getPaidAmount.get()) + amountDelta;
        setPaidAmount.accept(newPaidAmount);
        return PaymentStatusCalculator.calculate(totalCost, newPaidAmount);
    }
}
