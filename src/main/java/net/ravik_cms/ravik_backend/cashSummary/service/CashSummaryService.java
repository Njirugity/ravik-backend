package net.ravik_cms.ravik_backend.cashSummary.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budgetSummary.service.BudgetSummaryService;
import net.ravik_cms.ravik_backend.cashSummary.dtos.CashSummaryDto;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CashSummaryService {
    private final IncomeRepository incomeRepository;
    private final PaymentRepository paymentRepository;
    private final BudgetSummaryService budgetSummaryService;

    public CashSummaryDto getCashSummary(UUID projectId) {
        double totalIncome = nz(incomeRepository.sumAmountByProjectId(projectId));
        double totalPayments = nz(paymentRepository.sumAmountByProjectId(projectId));
        double totalActualCost = budgetSummaryService.getProjectBudgetSummary(projectId).getTotals().getTotalActualCost();
        double totalOutstanding = totalActualCost - totalPayments;
        double netCashPosition = totalIncome - totalPayments;
        double fundingGap = totalIncome - totalActualCost;

        return new CashSummaryDto(projectId, totalIncome, totalPayments, netCashPosition, totalActualCost, totalOutstanding, fundingGap);
    }

    private double nz(Double value) {
        return value != null ? value : 0.0;
    }
}
