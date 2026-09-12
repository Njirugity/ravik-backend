package net.ravik_cms.ravik_backend.cashSummary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CashSummaryDto {
    private UUID projectId;
    private Double totalIncome;
    private Double totalPayments;
    private Double netCashPosition;
    private Double totalActualCost;
    private Double totalOutstanding;
    private Double fundingGap;
}
