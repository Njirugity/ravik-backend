package net.ravik_cms.ravik_backend.milestoneBudget.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.attendance.AttendanceRepository;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.*;
import net.ravik_cms.ravik_backend.milestoneBudget.entity.MilestoneBudget;
import net.ravik_cms.ravik_backend.milestoneBudget.mapper.MilestoneBudgetMapper;
import net.ravik_cms.ravik_backend.milestoneBudget.repository.MilestoneBudgetRepository;
import net.ravik_cms.ravik_backend.milestones.Milestones;
import net.ravik_cms.ravik_backend.milestones.MilestonesRepository;
import net.ravik_cms.ravik_backend.phase.PhasesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MilestoneBudgetService {
    private final MilestoneBudgetRepository milestoneBudgetRepository;
    private final MilestonesRepository milestonesRepository;
    private final PhasesService phasesService;
    private final AttendanceRepository attendanceRepository;
    private final MilestoneBudgetMapper milestoneBudgetMapper;

    @Transactional
    public List<MilestoneBudgetLineDto> createBudgetLines(UUID milestoneId, List<CreateMilestoneBudgetLineDto> request) {
        Milestones milestone = milestonesRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

        milestoneBudgetRepository.deleteAllByMilestoneId(milestoneId);
        List<MilestoneBudget> lines = request.stream()
                .map(dto -> {
                    MilestoneBudget line = milestoneBudgetMapper.toEntity(dto);
                    line.setMilestone(milestone);
                    return line;
                }).toList();
        milestoneBudgetRepository.saveAll(lines);

        syncMilestoneBudget(milestone);
        return milestoneBudgetMapper.toLineDtoList(lines);
    }

    public List<MilestoneBudgetLineDto> getBudgetLines(UUID milestoneId) {
        return milestoneBudgetMapper.toLineDtoList(milestoneBudgetRepository.findAllByMilestoneId(milestoneId));
    }

    public MilestoneBudgetSummaryDto getBudgetSummary(UUID milestoneId) {
        List<MilestoneBudget> lines = milestoneBudgetRepository.findAllByMilestoneId(milestoneId);
        // Only DAILY-frequency staff are covered here; MONTHLY labour cost is not yet reflected.
        Double labourSpent = attendanceRepository.sumActualWagesPaidForMilestone(milestoneId);
        labourSpent = labourSpent != null ? labourSpent : 0.0;

        double totalBudget = 0.0;
        double totalSpent = 0.0;
        List<BudgetCategorySummaryDto> categories = new java.util.ArrayList<>();
        for (BudgetCategory category : BudgetCategory.values()) {
            double budgeted = lines.stream()
                    .filter(l -> l.getCategory() == category)
                    .mapToDouble(l -> l.getAmount() != null ? l.getAmount() : 0.0)
                    .sum();
            double spent = category == BudgetCategory.LABOUR ? labourSpent : 0.0;
            double variance = budgeted - spent;

            categories.add(new BudgetCategorySummaryDto(category, budgeted, spent, variance));
            totalBudget += budgeted;
            totalSpent += spent;
        }

        return new MilestoneBudgetSummaryDto(milestoneId, totalBudget, totalSpent, totalBudget - totalSpent, categories);
    }

    @Transactional
    public MilestoneBudgetLineDto updateBudgetLine(Long id, UpdateMilestoneBudgetLineDto request) {
        MilestoneBudget line = milestoneBudgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget line not found"));
        milestoneBudgetMapper.updateLine(request, line);
        milestoneBudgetRepository.save(line);
        syncMilestoneBudget(line.getMilestone());
        return milestoneBudgetMapper.toLineDto(line);
    }

    @Transactional
    public void deleteBudgetLine(Long id) {
        MilestoneBudget line = milestoneBudgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget line not found"));
        Milestones milestone = line.getMilestone();
        milestoneBudgetRepository.delete(line);
        syncMilestoneBudget(milestone);
    }

    private void syncMilestoneBudget(Milestones milestone) {
        Double total = milestoneBudgetRepository.sumAmountByMilestoneId(milestone.getId());
        milestone.setBudget(total != null ? total : 0.0);
        milestonesRepository.save(milestone);
        phasesService.syncPhaseBudget(milestone.getPhase().getId());
    }
}
