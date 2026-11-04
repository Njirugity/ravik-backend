package net.ravik_cms.ravik_backend.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.ProgressStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.dashboard.dto.AccountsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetCategoryDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.DashboardOverviewDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MaterialsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MilestoneDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PaymentsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.ScheduleDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.service.DashboardService;
import net.ravik_cms.ravik_backend.milestones.entity.Milestones;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.projects.entity.Projects;
import net.ravik_cms.ravik_backend.projects.repository.ProjectsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Default {@link DashboardService} implementation.
 * <p>
 * Project-scoped analytics across schedule, milestones, budget, payments, accounts and
 * materials. Permissions are checked in {@code DashboardController}, each section under the
 * read permission of the module it reports on. The section maths lives in the
 * {@code *Analytics} components; the overview reuses them so each figure has one definition.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProjectsRepository projectsRepository;
    private final MilestonesRepository milestonesRepository;
    private final ScheduleAnalytics scheduleAnalytics;
    private final MilestoneAnalytics milestoneAnalytics;
    private final FinanceAnalytics financeAnalytics;
    private final MaterialAnalytics materialAnalytics;

    @Override
    public DashboardOverviewDto getOverview(UUID projectId) {
        Projects project = findProject(projectId);
        List<Milestones> milestones = milestonesRepository.findAllByProjectId(projectId);

        BudgetCategoryDto budget = financeAnalytics.budgetTotals(projectId);
        double totalPaid = financeAnalytics.totalPaid(projectId);
        double totalIncome = financeAnalytics.totalIncome(projectId);

        return DashboardOverviewDto.builder()
                .projectId(projectId)
                .title(project.getTitle())
                .progress(project.getProgress())
                .percentComplete(scheduleAnalytics.percentComplete(milestones))
                .plannedEnd(project.getPlannedEnd())
                .forecastEnd(project.getForecastEnd())
                .scheduleVarianceDays(scheduleAnalytics.scheduleVarianceDays(project))
                .totalBudget(budget.approvedBudget())
                .totalActualCost(budget.actualCost())
                .budgetUsedPct(budget.utilisationPct())
                .totalPaid(totalPaid)
                .outstanding(budget.actualCost() - totalPaid)
                .totalIncome(totalIncome)
                .netCashPosition(totalIncome - totalPaid)
                .totalMilestones(milestones.size())
                .completedMilestones(milestones.stream().filter(m -> m.getStatus() == ProgressStatus.COMPLETED).count())
                .overdueMilestones(milestonesRepository.countOverdueMilestones(projectId, LocalDate.now()))
                .materialsAtRisk(materialAnalytics.atRiskCount(projectId))
                .build();
    }

    @Override
    public ScheduleDashboardDto getSchedule(UUID projectId) {
        return scheduleAnalytics.build(findProject(projectId), milestonesRepository.findAllByProjectId(projectId));
    }

    @Override
    public MilestoneDashboardDto getMilestones(UUID projectId, int upcomingDays) {
        findProject(projectId);
        return milestoneAnalytics.build(projectId, milestonesRepository.findAllByProjectId(projectId), upcomingDays);
    }

    @Override
    public BudgetDashboardDto getBudget(UUID projectId) {
        findProject(projectId);
        return financeAnalytics.buildBudget(projectId, milestonesRepository.findAllByProjectId(projectId));
    }

    @Override
    public PaymentsDashboardDto getPayments(UUID projectId, int months) {
        findProject(projectId);
        return financeAnalytics.buildPayments(projectId, months);
    }

    @Override
    public AccountsDashboardDto getAccounts(UUID projectId) {
        return financeAnalytics.buildAccounts(findProject(projectId));
    }

    @Override
    public MaterialsDashboardDto getMaterials(UUID projectId) {
        findProject(projectId);
        return materialAnalytics.build(projectId);
    }

    private Projects findProject(UUID projectId) {
        return projectsRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
