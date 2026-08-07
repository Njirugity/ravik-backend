package net.ravik_cms.ravik_backend.wages;

import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record WageHistoryProjection(
        Long id,
        Long memberId,
        String userName,
        String role,
        LocalDate startDate,
        LocalDate endDate,
        Double grossPay,
        PaymentFrequency frequency,
        LocalDateTime createdAt) {
}
