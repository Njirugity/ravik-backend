package net.ravik_cms.ravik_backend.expense.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.common.enums.PaymentStatus;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.common.utils.PaymentStatusCalculator;
import net.ravik_cms.ravik_backend.common.utils.ReferenceCodeGenerator;
import net.ravik_cms.ravik_backend.expense.dtos.CreateExpenseDto;
import net.ravik_cms.ravik_backend.expense.dtos.ExpenseInfoProjection;
import net.ravik_cms.ravik_backend.expense.dtos.UpdateExpenseDto;
import net.ravik_cms.ravik_backend.expense.entity.Expenses;
import net.ravik_cms.ravik_backend.expense.mapper.ExpenseMapper;
import net.ravik_cms.ravik_backend.expense.repository.ExpenseRepository;
import net.ravik_cms.ravik_backend.expenseCategory.entity.ExpenseCategory;
import net.ravik_cms.ravik_backend.expenseCategory.repository.ExpenseCategoryRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ProjectsRepository projectsRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final ExpenseMapper expenseMapper;

    public void addExpense(CreateExpenseDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        ExpenseCategory category = expenseCategoryRepository.findById(request.getCategoryId()).orElseThrow(() ->
                new ResourceNotFoundException("Expense category not found"));
        Expenses expense = expenseMapper.toEntity(request);
        expense.setProject(project);
        expense.setCategory(category);
        expense.setReferenceCode(ReferenceCodeGenerator.generate("EX", expenseRepository::existsByReferenceCode));
        expense.setPaymentStatus(PaymentStatus.PENDING);
        expenseRepository.save(expense);
    }

    public Page<ExpenseInfoProjection> getExpenses(UUID projectId, String search, Pageable pageable) {
        return expenseRepository.findAllByProject(projectId, search, pageable);
    }

    public void updateExpense(UUID id, UpdateExpenseDto request) {
        Expenses expense = expenseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense not found"));
        expenseMapper.updateExpense(request, expense);
        if (request.getCategoryId() != null) {
            ExpenseCategory category = expenseCategoryRepository.findById(request.getCategoryId()).orElseThrow(() ->
                    new ResourceNotFoundException("Expense category not found"));
            expense.setCategory(category);
        }
        expense.setPaymentStatus(PaymentStatusCalculator.calculate(expense.getAmount(), expense.getPaidAmount()));
        expenseRepository.save(expense);
    }

    public void deleteExpense(UUID id) {
        Expenses expense = expenseRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Expense not found"));
        expenseRepository.delete(expense);
    }
}
