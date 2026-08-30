package net.ravik_cms.ravik_backend.wages;

import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;

import java.time.LocalDate;
import java.util.UUID;

public record WageCalculationDataProjection(Long membershipId, String userName, String jobTitle,
                                            Double baseWage, Long workedDays,
                                            PaymentFrequency frequency, UUID milestoneId, String milestoneName) {
}
