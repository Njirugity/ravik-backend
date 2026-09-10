package net.ravik_cms.ravik_backend.budgetSummary.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budget.repository.BudgetRepository;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.BudgetCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.MilestoneBudgetVsActualDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.MilestoneCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.PhaseBudgetSummaryDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.PhaseCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.ProjectBudgetSummaryDto;
import net.ravik_cms.ravik_backend.common.dtos.CategoryAmountProjection;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.equipmentsPayout.repository.EquipmentsPayoutRepository;
import net.ravik_cms.ravik_backend.expense.repository.ExpenseRepository;
import net.ravik_cms.ravik_backend.labourPayout.repository.LabourPayoutRepository;
import net.ravik_cms.ravik_backend.milestoneBudget.repository.MilestoneBudgetRepository;
import net.ravik_cms.ravik_backend.milestones.repository.MilestonesRepository;
import net.ravik_cms.ravik_backend.phase.repository.PhasesRepository;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.subContractorPayout.repository.SubContractorPayoutRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetSummaryService {
    private final BudgetRepository budgetRepository;
    private final MilestoneBudgetRepository milestoneBudgetRepository;
    private final EquipmentsPayoutRepository equipmentsPayoutRepository;
    private final SubContractorPayoutRepository subContractorPayoutRepository;
    private final LabourPayoutRepository labourPayoutRepository;
    private final ExpenseRepository expenseRepository;
    private final ProjectsRepository projectsRepository;
    private final PhasesRepository phasesRepository;
    private final MilestonesRepository milestonesRepository;

    public ProjectBudgetSummaryDto getProjectBudgetSummary(UUID projectId) {
        if (!projectsRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found");
        }
        Map<BudgetCategory, Double> approved = toMap(budgetRepository.sumApprovedBudgetByProjectGroupByCategory(projectId));
        Map<BudgetCategory, Double> rolledUp = toMap(milestoneBudgetRepository.sumByProjectGroupByCategory(projectId));

        Map<BudgetCategory, Double> milestoneCost = new EnumMap<>(BudgetCategory.class);
        milestoneCost.put(BudgetCategory.PLANT_AND_EQUIPMENT, nz(equipmentsPayoutRepository.sumActualCostByProjectId(projectId)));
        milestoneCost.put(BudgetCategory.SUBCONTRACTOR, nz(subContractorPayoutRepository.sumActualCostByProjectId(projectId)));
        milestoneCost.put(BudgetCategory.LABOUR, nz(labourPayoutRepository.sumTotalAmountByProjectId(projectId)));

        Map<BudgetCategory, Double> unscoped = toMap(expenseRepository.sumByProjectGroupByBudgetCategory(projectId));

        List<BudgetCategoryLineDto> lines = new ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            lines.add(buildProjectLine(category,
                    approved.getOrDefault(category, 0.0),
                    rolledUp.getOrDefault(category, 0.0),
                    milestoneCost.getOrDefault(category, 0.0),
                    unscoped.getOrDefault(category, 0.0)));
        }
        return new ProjectBudgetSummaryDto(projectId, lines, aggregateProjectTotals(lines));
    }

    public PhaseBudgetSummaryDto getPhaseBudgetSummary(UUID phaseId) {
        if (!phasesRepository.existsById(phaseId)) {
            throw new ResourceNotFoundException("Phase not found");
        }
        Map<BudgetCategory, Double> rolledUp = toMap(milestoneBudgetRepository.sumByPhaseGroupByCategory(phaseId));

        Map<BudgetCategory, Double> milestoneCost = new EnumMap<>(BudgetCategory.class);
        milestoneCost.put(BudgetCategory.PLANT_AND_EQUIPMENT, nz(equipmentsPayoutRepository.sumActualCostByPhaseId(phaseId)));
        milestoneCost.put(BudgetCategory.LABOUR, nz(subContractorPayoutRepository.sumActualCostByPhaseId(phaseId)));

        List<PhaseCategoryLineDto> lines = new ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            double rolled = rolledUp.getOrDefault(category, 0.0);
            double cost = milestoneCost.getOrDefault(category, 0.0);
            lines.add(new PhaseCategoryLineDto(category, rolled, cost, cost, rolled - cost));
        }
        return new PhaseBudgetSummaryDto(phaseId, lines, aggregatePhaseTotals(lines));
    }

    public MilestoneBudgetVsActualDto getMilestoneBudgetVsActual(UUID milestoneId) {
        if (!milestonesRepository.existsById(milestoneId)) {
            throw new ResourceNotFoundException("Milestone not found");
        }
        Map<BudgetCategory, Double> budget = toMap(milestoneBudgetRepository.sumByMilestoneGroupByCategory(milestoneId));

        Map<BudgetCategory, Double> actual = new EnumMap<>(BudgetCategory.class);
        actual.put(BudgetCategory.PLANT_AND_EQUIPMENT, nz(equipmentsPayoutRepository.sumActualCostByMilestoneId(milestoneId)));
        actual.put(BudgetCategory.LABOUR, nz(subContractorPayoutRepository.sumActualCostByMilestoneId(milestoneId)));

        List<MilestoneCategoryLineDto> lines = new ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            double b = budget.getOrDefault(category, 0.0);
            double a = actual.getOrDefault(category, 0.0);
            lines.add(new MilestoneCategoryLineDto(category, b, a, b - a));
        }
        return new MilestoneBudgetVsActualDto(milestoneId, lines, aggregateMilestoneTotals(lines));
    }

    private BudgetCategoryLineDto buildProjectLine(BudgetCategory category, double approved, double rolledUp, double milestoneCost, double unscopedExpense) {
        double totalActualCost = milestoneCost + unscopedExpense;
        return new BudgetCategoryLineDto(category, approved, rolledUp, milestoneCost, unscopedExpense, totalActualCost,
                approved - rolledUp, approved - totalActualCost);
    }

    private BudgetCategoryLineDto aggregateProjectTotals(List<BudgetCategoryLineDto> lines) {
        double approved = lines.stream().mapToDouble(BudgetCategoryLineDto::getApprovedBudget).sum();
        double rolledUp = lines.stream().mapToDouble(BudgetCategoryLineDto::getRolledUpBudget).sum();
        double milestoneCost = lines.stream().mapToDouble(BudgetCategoryLineDto::getMilestoneCost).sum();
        double unscopedExpense = lines.stream().mapToDouble(BudgetCategoryLineDto::getUnscopedExpense).sum();
        double totalActualCost = milestoneCost + unscopedExpense;
        return new BudgetCategoryLineDto(null, approved, rolledUp, milestoneCost, unscopedExpense, totalActualCost,
                approved - rolledUp, approved - totalActualCost);
    }

    private PhaseCategoryLineDto aggregatePhaseTotals(List<PhaseCategoryLineDto> lines) {
        double rolledUp = lines.stream().mapToDouble(PhaseCategoryLineDto::getRolledUpBudget).sum();
        double cost = lines.stream().mapToDouble(PhaseCategoryLineDto::getMilestoneCost).sum();
        return new PhaseCategoryLineDto(null, rolledUp, cost, cost, rolledUp - cost);
    }

    private MilestoneCategoryLineDto aggregateMilestoneTotals(List<MilestoneCategoryLineDto> lines) {
        double budget = lines.stream().mapToDouble(MilestoneCategoryLineDto::getBudget).sum();
        double actual = lines.stream().mapToDouble(MilestoneCategoryLineDto::getActualCost).sum();
        return new MilestoneCategoryLineDto(null, budget, actual, budget - actual);
    }

    private Map<BudgetCategory, Double> toMap(List<CategoryAmountProjection> projections) {
        Map<BudgetCategory, Double> map = new EnumMap<>(BudgetCategory.class);
        for (CategoryAmountProjection projection : projections) {
            map.put(projection.category(), projection.total());
        }
        return map;
    }

    private double nz(Double value) {
        return value != null ? value : 0.0;
    }
}
