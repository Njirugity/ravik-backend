package net.ravik_cms.ravik_backend.accountInsights.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ClientAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInsightsService {
    private final AccountRepository accountRepository;
    private final IncomeRepository incomeRepository;
    private final PaymentRepository paymentRepository;

    public List<ClientAccountInsightDto> getClientDashboard(UUID clientId) {
        return accountRepository.findAllByClientId(clientId).stream()
                .map(this::toClientInsight)
                .toList();
    }

    public List<ProjectAccountInsightDto> getProjectAccountFlows(UUID projectId) {
        return accountRepository.findAllUsedInProject(projectId).stream()
                .map(account -> toProjectInsight(projectId, account))
                .toList();
    }

    private ClientAccountInsightDto toClientInsight(Accounts account) {
        double openingBalance = nz(account.getOpeningBalance());
        double totalIncome = nz(incomeRepository.sumAmountByAccountId(account.getId()));
        double totalPayments = nz(paymentRepository.sumAmountByAccountId(account.getId()));
        double currentBalance = openingBalance + totalIncome - totalPayments;
        return new ClientAccountInsightDto(account.getId(), account.getName(), account.getType(),
                openingBalance, totalIncome, totalPayments, currentBalance);
    }

    private ProjectAccountInsightDto toProjectInsight(UUID projectId, Accounts account) {
        double incomeThisProject = nz(incomeRepository.sumAmountByProjectIdAndAccountId(projectId, account.getId()));
        double paymentsThisProject = nz(paymentRepository.sumAmountByProjectIdAndAccountId(projectId, account.getId()));
        return new ProjectAccountInsightDto(account.getId(), account.getName(), account.getType(),
                incomeThisProject, paymentsThisProject);
    }

    private double nz(Double value) {
        return value != null ? value : 0.0;
    }
}