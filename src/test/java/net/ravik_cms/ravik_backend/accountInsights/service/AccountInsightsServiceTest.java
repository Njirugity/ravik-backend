package net.ravik_cms.ravik_backend.accountInsights.service;

import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.accountInsights.dtos.AccountProjectFlowDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.AccountProjectSum;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ClientAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountInsightsServiceTest {

    private AccountRepository accountRepository;
    private IncomeRepository incomeRepository;
    private PaymentRepository paymentRepository;
    private ProjectsRepository projectsRepository;
    private AccountInsightsService service;
    private final Client client = new Client();
    private final Accounts account = new Accounts();

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        incomeRepository = mock(IncomeRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        projectsRepository = mock(ProjectsRepository.class);
        service = new AccountInsightsService(accountRepository, incomeRepository, paymentRepository, projectsRepository);

        client.setId(UUID.randomUUID());
        account.setId(UUID.randomUUID());
        account.setName("Bank");
        account.setOpeningBalance(1000.0);
        account.setClient(client);
        when(accountRepository.findAllByClientId(client.getId())).thenReturn(List.of(account));
    }

    @Test
    void mergesIncomeAndPaymentsPerProject() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        when(incomeRepository.sumAmountByAccountId(account.getId())).thenReturn(500.0);
        when(paymentRepository.sumAmountByAccountId(account.getId())).thenReturn(300.0);
        when(incomeRepository.sumAmountByAccountIdGroupedByProject(account.getId()))
                .thenReturn(List.of(new AccountProjectSum(a, "Alpha", 500.0)));
        when(paymentRepository.sumAmountByAccountIdGroupedByProject(account.getId()))
                .thenReturn(List.of(new AccountProjectSum(a, "Alpha", 100.0), new AccountProjectSum(b, "Beta", 200.0)));

        ClientAccountInsightDto dto = service.getClientDashboard(client.getId()).getFirst();

        assertEquals(1200.0, dto.getCurrentBalance());
        assertEquals(List.of(new AccountProjectFlowDto(a, "Alpha", 500.0, 100.0),
                new AccountProjectFlowDto(b, "Beta", 0.0, 200.0)), dto.getProjects());
    }

    @Test
    void listsUnusedClientAccountsWithZeroProjectFigures() {
        Projects project = new Projects();
        project.setId(UUID.randomUUID());
        project.setClient(client);
        when(projectsRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(incomeRepository.sumAmountByProjectIdAndAccountId(any(), any())).thenReturn(null);
        when(paymentRepository.sumAmountByProjectIdAndAccountId(any(), any())).thenReturn(null);

        List<ProjectAccountInsightDto> rows = service.getClientAccountsForProject(project.getId());

        assertEquals(List.of(new ProjectAccountInsightDto(account.getId(), "Bank", null, 0.0, 0.0)), rows);
    }
}
