package net.ravik_cms.ravik_backend.budget.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.budget.dtos.BudgetInfoProjection;
import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;
import net.ravik_cms.ravik_backend.budget.dtos.UpdateBudgetDto;
import net.ravik_cms.ravik_backend.budget.entity.Budget;
import net.ravik_cms.ravik_backend.budget.mapper.BudgetMapper;
import net.ravik_cms.ravik_backend.budget.repository.BudgetRepository;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.BudgetSource;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final ProjectsRepository projectsRepository;
    private final BudgetMapper budgetMapper;

    public void addBudget(CreateBudgetDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        Budget budget = budgetMapper.toEntity(request);
        budget.setProjects(project);
        budget.setSource(BudgetSource.MANUAL);
        budgetRepository.save(budget);
    }

    public Page<BudgetInfoProjection> getBudgets(UUID projectId, BudgetCategory category, Pageable pageable) {
        return budgetRepository.findAllByProject(projectId, category, pageable);
    }

    public void updateBudget(UUID id, UpdateBudgetDto request) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Budget not found"));
        budgetMapper.updateBudget(request, budget);
        budgetRepository.save(budget);
    }

    public void deleteBudget(UUID id) {
        Budget budget = budgetRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Budget not found"));
        budgetRepository.delete(budget);
    }
}
