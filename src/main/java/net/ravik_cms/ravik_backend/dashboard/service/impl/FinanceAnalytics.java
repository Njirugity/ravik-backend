package net.ravik_cms.ravik_backend.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ClientAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.dtos.ProjectAccountInsightDto;
import net.ravik_cms.ravik_backend.accountInsights.service.AccountInsightsService;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.BudgetCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.ProjectBudgetSummaryDto;
import net.ravik_cms.ravik_backend.budgetSummary.service.BudgetSummaryService;
import net.ravik_cms.ravik_backend.common.dtos.MilestoneAmountProjection;
import net.ravik_cms.ravik_backend.common.dtos.MonthAmountProjection;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostCategoryLineDto;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.costSummary.service.CostSummaryService;
import net.ravik_cms.ravik_backend.dashboard.dto.AccountDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.AccountsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetCategoryDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.CategoryPaymentDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MilestoneOverrunDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MonthlyCashFlowDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PayablesStatusDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PaymentsDashboardDto;
import net.ravik_cms.ravik_backend.equipmentsPayout.repository.EquipmentsPayoutRepository;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.milestoneBudget.repository.MilestoneBudgetRepository;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.payment.dtos.PayoutSummaryProjection;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import net.ravik_cms.ravik_backend.payment.service.PaymentService;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.subContractorPayout.repository.SubContractorPayoutRepository;
import net.ravik_cms.ravik_backend.wages.WagesRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds the budget, payments and accounts sections of the dashboard.
 * <p>
 * Category figures come from {@link BudgetSummaryService} and {@link CostSummaryService} so the
 * dashboard always agrees with the standalone summary endpoints.
 */
@Component
@RequiredArgsConstructor
class FinanceAnalytics {
    private static final int TOP_OVERRUNS = 5;

    private final BudgetSummaryService budgetSummaryService;
    private final CostSummaryService costSummaryService;
    private final AccountInsightsService accountInsightsService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final IncomeRepository incomeRepository;
    private final MilestoneBudgetRepository milestoneBudgetRepository;
    private final EquipmentsPayoutRepository equipmentsPayoutRepository;
    private final SubContractorPayoutRepository subContractorPayoutRepository;
    private final WagesRepository wagesRepository;

    BudgetDashboardDto buildBudget(UUID projectId, List<Milestones> milestones) {
        ProjectBudgetSummaryDto summary = budgetSummaryService.getProjectBudgetSummary(projectId);
        return BudgetDashboardDto.builder()
                .categories(summary.getCategories().stream().map(this::toBudgetCategory).toList())
                .totals(toBudgetCategory(summary.getTotals()))
                .topMilestoneOverruns(topMilestoneOverruns(projectId, milestones))
                .build();
    }

    /**
     * Approved budget and actual cost totals, for the overview tiles.
     */
    BudgetCategoryDto budgetTotals(UUID projectId) {
        return toBudgetCategory(budgetSummaryService.getProjectBudgetSummary(projectId).getTotals());
    }

    double totalPaid(UUID projectId) {
        return nz(paymentRepository.sumAmountByProjectId(projectId));
    }

    double totalIncome(UUID projectId) {
        return nz(incomeRepository.sumAmountByProjectId(projectId));
    }

    PaymentsDashboardDto buildPayments(UUID projectId, int months) {
        CostPaymentSummaryDto costSummary = costSummaryService.getCostPaymentSummary(projectId);
        double totalIncome = totalIncome(projectId);
        CostCategoryLineDto totals = costSummary.getTotals();

        return PaymentsDashboardDto.builder()
                .totalIncome(totalIncome)
                .totalPaid(nz(totals.getTotalPaid()))
                .netCashPosition(totalIncome - nz(totals.getTotalPaid()))
                .totalActualCost(nz(totals.getTotalActualCost()))
                .outstanding(nz(totals.getOutstanding()))
                .byCategory(costSummary.getCategories().stream()
                        .map(line -> CategoryPaymentDto.builder()
                                .category(line.getCategory())
                                .actualCost(nz(line.getTotalActualCost()))
                                .paid(nz(line.getTotalPaid()))
                                .outstanding(nz(line.getOutstanding()))
                                .paymentProgressPct(line.getPaymentProgressPct())
                                .build())
                        .toList())
                .payablesByStatus(payablesByStatus(projectId))
                .monthlyTrend(monthlyTrend(projectId, months))
                .build();
    }

    AccountsDashboardDto buildAccounts(Projects project) {
        Map<UUID, ClientAccountInsightDto> clientAccounts = project.getClient() == null
                ? Map.of()
                : accountInsightsService.getClientDashboard(project.getClient().getId()).stream()
                        .collect(Collectors.toMap(ClientAccountInsightDto::getAccountId, Function.identity()));

        List<AccountDashboardDto> accounts = accountInsightsService.getProjectAccountFlows(project.getId()).stream()
                .map(flow -> toAccount(flow, clientAccounts.get(flow.getAccountId())))
                .toList();

        return AccountsDashboardDto.builder()
                .accounts(accounts)
                .totalIncome(accounts.stream().mapToDouble(AccountDashboardDto::incomeThisProject).sum())
                .totalPayments(accounts.stream().mapToDouble(AccountDashboardDto::paymentsThisProject).sum())
                .build();
    }

    private AccountDashboardDto toAccount(ProjectAccountInsightDto flow, ClientAccountInsightDto clientAccount) {
        return AccountDashboardDto.builder()
                .accountId(flow.getAccountId())
                .name(flow.getName())
                .type(flow.getType())
                .incomeThisProject(nz(flow.getIncomeThisProject()))
                .paymentsThisProject(nz(flow.getPaymentsThisProject()))
                .currentBalance(clientAccount == null ? null : clientAccount.getCurrentBalance())
                .build();
    }

    private BudgetCategoryDto toBudgetCategory(BudgetCategoryLineDto line) {
        double approved = nz(line.getApprovedBudget());
        double actual = nz(line.getTotalActualCost());
        return BudgetCategoryDto.builder()
                .category(line.getCategory())
                .approvedBudget(approved)
                .allocatedBudget(nz(line.getRolledUpBudget()))
                .actualCost(actual)
                .remaining(approved - actual)
                .utilisationPct(approved == 0 ? null : actual / approved)
                .build();
    }

    /**
     * Milestone actuals are equipment + subcontractor payouts + wages booked to the milestone;
     * expenses and material deliveries aren't tied to milestones, so they can't overrun one.
     */
    private List<MilestoneOverrunDto> topMilestoneOverruns(UUID projectId, List<Milestones> milestones) {
        Map<UUID, Double> budget = toMap(milestoneBudgetRepository.sumByProjectGroupByMilestone(projectId));
        Map<UUID, Double> actual = new HashMap<>();
        for (List<MilestoneAmountProjection> costs : List.of(
                equipmentsPayoutRepository.sumActualCostByProjectGroupByMilestone(projectId),
                subContractorPayoutRepository.sumActualCostByProjectGroupByMilestone(projectId),
                wagesRepository.sumGrossPayByProjectGroupByMilestone(projectId))) {
            toMap(costs).forEach((milestoneId, amount) -> actual.merge(milestoneId, amount, Double::sum));
        }

        return milestones.stream()
                .map(m -> {
                    double b = budget.getOrDefault(m.getId(), 0.0);
                    double a = actual.getOrDefault(m.getId(), 0.0);
                    return new MilestoneOverrunDto(m.getId(), m.getTitle(), b, a, a - b);
                })
                .filter(row -> row.overrun() > 0)
                .sorted(Comparator.comparingDouble(MilestoneOverrunDto::overrun).reversed())
                .limit(TOP_OVERRUNS)
                .toList();
    }

    /**
     * Reuses the payouts list behind {@code GET /payments/payouts}, so every payable source
     * (labour, equipment, subcontractor, expenses, materials) is counted the same way.
     */
    private List<PayablesStatusDto> payablesByStatus(UUID projectId) {
        List<PayoutSummaryProjection> payables = paymentService
                .getAllPayouts(projectId, null, null, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        Map<PaymentStatus, List<PayoutSummaryProjection>> byStatus = new EnumMap<>(PaymentStatus.class);
        for (PaymentStatus status : PaymentStatus.values()) {
            byStatus.put(status, new ArrayList<>());
        }
        for (PayoutSummaryProjection payable : payables) {
            PaymentStatus status = payable.paymentStatus() != null ? payable.paymentStatus() : PaymentStatus.PENDING;
            byStatus.get(status).add(payable);
        }

        return byStatus.entrySet().stream()
                .map(entry -> {
                    double totalCost = entry.getValue().stream().mapToDouble(p -> nz(p.totalCost())).sum();
                    double paid = entry.getValue().stream().mapToDouble(p -> nz(p.paidAmount())).sum();
                    return PayablesStatusDto.builder()
                            .status(entry.getKey())
                            .count(entry.getValue().size())
                            .totalCost(totalCost)
                            .paid(paid)
                            .outstanding(Math.max(0, totalCost - paid))
                            .build();
                })
                .toList();
    }

    /**
     * One row per month for the last {@code months} months (current month included), with
     * zeros for months that had no movement so charts get a continuous axis.
     */
    private List<MonthlyCashFlowDto> monthlyTrend(UUID projectId, int months) {
        YearMonth current = YearMonth.now();
        YearMonth first = current.minusMonths(months - 1L);
        LocalDate from = first.atDay(1);

        Map<YearMonth, Double> paid = toMonthMap(paymentRepository.sumByProjectGroupByMonth(projectId, from));
        Map<YearMonth, Double> income = toMonthMap(incomeRepository.sumByProjectGroupByMonth(projectId, from));

        List<MonthlyCashFlowDto> trend = new ArrayList<>();
        for (YearMonth month = first; !month.isAfter(current); month = month.plusMonths(1)) {
            double in = income.getOrDefault(month, 0.0);
            double out = paid.getOrDefault(month, 0.0);
            trend.add(new MonthlyCashFlowDto(month.toString(), in, out, in - out));
        }
        return trend;
    }

    private Map<UUID, Double> toMap(List<MilestoneAmountProjection> rows) {
        return rows.stream()
                .filter(row -> row.milestoneId() != null)
                .collect(Collectors.toMap(MilestoneAmountProjection::milestoneId, row -> nz(row.total()), Double::sum));
    }

    private Map<YearMonth, Double> toMonthMap(List<MonthAmountProjection> rows) {
        return rows.stream()
                .filter(row -> row.year() != null && row.month() != null)
                .collect(Collectors.toMap(row -> YearMonth.of(row.year(), row.month()), row -> nz(row.total()), Double::sum));
    }

    private double nz(Double value) {
        return value != null ? value : 0.0;
    }
}
