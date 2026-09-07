package net.ravik_cms.ravik_backend.payment.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaymentDto {
    private LocalDate datePaid;
    private Double amount;
    private UUID accountId;
    private String transactionCode;
}
