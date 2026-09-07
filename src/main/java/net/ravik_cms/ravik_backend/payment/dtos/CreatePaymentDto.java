package net.ravik_cms.ravik_backend.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentDto {
    private LocalDate datePaid;
    private String payee;
    private Double amount;
    private PaymentCategory paymentCategory;
    private UUID referenceId;
    private UUID accountId;
    private String transactionCode;
}
