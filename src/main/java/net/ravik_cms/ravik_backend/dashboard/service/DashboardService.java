package net.ravik_cms.ravik_backend.dashboard.service;

import net.ravik_cms.ravik_backend.dashboard.dto.AccountsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.BudgetDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.DashboardOverviewDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MaterialsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.MilestoneDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.PaymentsDashboardDto;
import net.ravik_cms.ravik_backend.dashboard.dto.ScheduleDashboardDto;

import java.util.UUID;

public interface DashboardService {
    DashboardOverviewDto getOverview(UUID projectId);
    ScheduleDashboardDto getSchedule(UUID projectId);
    MilestoneDashboardDto getMilestones(UUID projectId, int upcomingDays);
    BudgetDashboardDto getBudget(UUID projectId);
    PaymentsDashboardDto getPayments(UUID projectId, int months);
    AccountsDashboardDto getAccounts(UUID projectId);
    MaterialsDashboardDto getMaterials(UUID projectId);
}
