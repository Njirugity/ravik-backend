package net.ravik_cms.ravik_backend.costSummary.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.BudgetCategoryLineDto;
import net.ravik_cms.ravik_backend.budgetSummary.dtos.ProjectBudgetSummaryDto;
import net.ravik_cms.ravik_backend.budgetSummary.service.BudgetSummaryService;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentCategory;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostCategoryLineDto;
import net.ravik_cms.ravik_backend.costSummary.dtos.CostPaymentSummaryDto;
import net.ravik_cms.ravik_backend.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CostSummaryService {
    private final BudgetSummaryService budgetSummaryService;
    private final PaymentRepository paymentRepository;

    public CostPaymentSummaryDto getCostPaymentSummary(UUID projectId) {
        ProjectBudgetSummaryDto budgetSummary = budgetSummaryService.getProjectBudgetSummary(projectId);

        Map<BudgetCategory, Double> paidByCategory = new EnumMap<>(BudgetCategory.class);
        paidByCategory.put(BudgetCategory.PLANT_AND_EQUIPMENT,
                nz(paymentRepository.sumAmountByProjectIdAndPaymentCategoryIn(projectId, List.of(PaymentCategory.EQUIPMENT))));
        paidByCategory.put(BudgetCategory.LABOUR,
                nz(paymentRepository.sumAmountByProjectIdAndPaymentCategoryIn(projectId, List.of(PaymentCategory.LABOUR, PaymentCategory.SUBCONTRACTOR))));

        List<CostCategoryLineDto> lines = new ArrayList<>();
        for (BudgetCategoryLineDto line : budgetSummary.getCategories()) {
            lines.add(buildLine(line.getCategory(), line.getTotalActualCost(), paidByCategory.getOrDefault(line.getCategory(), 0.0)));
        }

        double totalActualCost = budgetSummary.getTotals().getTotalActualCost();
        double totalPaid = nz(paymentRepository.sumAmountByProjectId(projectId));
        CostCategoryLineDto totals = buildLine(null, totalActualCost, totalPaid);

        return new CostPaymentSummaryDto(projectId, lines, totals);
    }

    private CostCategoryLineDto buildLine(BudgetCategory category, double totalActualCost, double totalPaid) {
        double outstanding = totalActualCost - totalPaid;
        Double paymentProgressPct = totalActualCost == 0 ? null : totalPaid / totalActualCost;
        return new CostCategoryLineDto(category, totalActualCost, totalPaid, outstanding, paymentProgressPct);
    }

    private double nz(Double value) {
        return value != null ? value : 0.0;
    }
}
