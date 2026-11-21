package net.ravik_cms.ravik_backend.accountInsights.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientAccountInsightDto {
    private UUID accountId;
    private String name;
    private String type;
    private Double openingBalance;
    private Double totalIncome;
    private Double totalPayments;
    private Double currentBalance;
    /** This account's income and payments in each of the client's projects that used it. */
    private List<AccountProjectFlowDto> projects;
}
