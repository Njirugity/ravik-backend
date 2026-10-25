package net.ravik_cms.ravik_backend.materials.dto;

import lombok.Builder;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
public record MaterialDeliveredDto(
        UUID id,
        UUID materialListId,
        String materialName,
        String materialMetric,
        UUID projectId,
        Double quantityDelivered,
        Double unitPrice,
        Double totalCost,
        String referenceCode,
        Double paidAmount,
        PaymentStatus paymentStatus,
        LocalDate dateDelivered,
        UUID createdByUserId,
        String createdByUserName,
        Long createdAt,
        Long updatedAt
) {
}
