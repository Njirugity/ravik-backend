package net.ravik_cms.ravik_backend.wages;

import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

import java.time.LocalDate;

public record WageCalculationDataProjection(Long membershipId, String userName, String jobTitle,
                                            Double baseWage, Long workedDays,
                                            PaymentFrequency frequency) {
}
