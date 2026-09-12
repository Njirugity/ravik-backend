package net.ravik_cms.ravik_backend.payment.dtos;

import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PaymentInfoProjection(
        UUID id,
        LocalDate datePaid,
        String payee,
        Double amount,
        PaymentCategory paymentCategory,
        UUID referenceId,
        String referenceCode,
        UUID projectId,
        UUID accountId,
        PaymentStatus paymentStatus,
        String transactionCode,
        Double amountDue,
        Double totalPaidAmount
        ) {

    public PaymentInfoProjection(
            UUID id,
            LocalDate datePaid,
            String payee,
            Double amount,
            PaymentCategory paymentCategory,
            UUID referenceId,
            String referenceCode,
            UUID projectId,
            UUID accountId,
            PaymentStatus paymentStatus,
            String transactionCode) {
        this(id, datePaid, payee, amount, paymentCategory, referenceId, referenceCode, projectId, accountId,
                paymentStatus, transactionCode, null, null);
    }
}
