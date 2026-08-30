package net.ravik_cms.ravik_backend.common.utils;

import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

public final class PaymentStatusCalculator {
    private PaymentStatusCalculator() {
    }

    public static PaymentStatus calculate(double totalCost, Double paidAmount) {
        if (paidAmount == null || paidAmount <= 0) {
            return PaymentStatus.PENDING;
        }
        if (paidAmount < totalCost) {
            return PaymentStatus.PARTIALLY_PAID;
        }
        if (paidAmount > totalCost) {
            return PaymentStatus.OVERPAID;
        }
        return PaymentStatus.PAID;
    }
}
