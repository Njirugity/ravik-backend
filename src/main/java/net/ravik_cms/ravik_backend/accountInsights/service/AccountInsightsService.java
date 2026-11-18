package net.ravik_cms.ravik_backend.accountInsights.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.accountInsights.dtos.AccountProjectFlowDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.AccountProjectSum;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ClientAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInsightsService {
    private final AccountRepository accountRepository;
    private final IncomeRepository incomeRepository;
    private final PaymentRepository paymentRepository;
    private final ProjectsRepository projectsRepository;

    public List<ClientAccountInsightDto> getClientDashboard(UUID clientId) {
        return accountRepository.findAllByClientId(clientId).stream()
                .map(this::toClientInsight)
                .toList();
    }

    /**
     * Every account the project's client owns, with this project's figures. Unlike
     * {@link #getProjectAccountFlows}, accounts with no activity here are included, so an account
     * created in another project (or not used yet) can still be seen and picked.
     */
    public List<ProjectAccountInsightDto> getClientAccountsForProject(UUID projectId) {
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (project.getClient() == null) {
            return List.of();
        }
        return accountRepository.findAllByClientId(project.getClient().getId()).stream()
                .map(account -> toProjectInsight(projectId, account))
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
                openingBalance, totalIncome, totalPayments, currentBalance, projectFlows(account.getId()));
    }

    /** Income and payments per project for one account, merged on project id, by title. */
    private List<AccountProjectFlowDto> projectFlows(UUID accountId) {
        Map<UUID, AccountProjectFlowDto> byProject = new LinkedHashMap<>();
        for (AccountProjectSum sum : incomeRepository.sumAmountByAccountIdGroupedByProject(accountId)) {
            flowFor(byProject, sum).setIncome(nz(sum.amount()));
        }
        for (AccountProjectSum sum : paymentRepository.sumAmountByAccountIdGroupedByProject(accountId)) {
            flowFor(byProject, sum).setPayments(nz(sum.amount()));
        }
        return byProject.values().stream()
                .sorted(Comparator.comparing(AccountProjectFlowDto::getProjectTitle,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private AccountProjectFlowDto flowFor(Map<UUID, AccountProjectFlowDto> byProject, AccountProjectSum sum) {
        return byProject.computeIfAbsent(sum.projectId(),
                id -> new AccountProjectFlowDto(id, sum.projectTitle(), 0.0, 0.0));
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