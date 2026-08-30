package net.ravik_cms.ravik_backend.expenseCategory.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.expenseCategory.dto.CreateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.dto.ExpenseCategoryInfoProjection;
import net.ravik_cms.ravik_backend.expenseCategory.dto.UpdateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import net.ravik_cms.ravik_backend.expenseCategory.mapper.ExpenseCategoryMapper;
import net.ravik_cms.ravik_backend.expenseCategory.repository.ExpenseCategoryRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryService {
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ProjectsRepository projectsRepository;
    private final ExpenseCategoryMapper expenseCategoryMapper;

    public void addExpenseCategory(CreateExpenseCategoryDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        ExpenseCategory category = expenseCategoryMapper.toEntity(request);
        category.setProjects(project);
        if (category.getBudgetCategory() == null) {
            category.setBudgetCategory(BudgetCategory.OTHERS);
        }
        expenseCategoryRepository.save(category);
    }

    public Page<ExpenseCategoryInfoProjection> getExpenseCategories(UUID projectId, String search, Pageable pageable) {
        return expenseCategoryRepository.findAllByProject(projectId, search, pageable);
    }

    public void updateExpenseCategory(Long id, UpdateExpenseCategoryDto request) {
        ExpenseCategory category = expenseCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense category not found"));
        expenseCategoryMapper.updateExpenseCategory(request, category);
        expenseCategoryRepository.save(category);
    }

    public void deleteExpenseCategory(Long id) {
        ExpenseCategory category = expenseCategoryRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense category not found"));
        expenseCategoryRepository.delete(category);
    }
}
