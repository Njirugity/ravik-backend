package net.ravik_cms.ravik_backend.income.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import net.ravik_cms.ravik_backend.income.dtos.CreateIncomeDto;
import net.ravik_cms.ravik_backend.income.dtos.IncomeInfoProjection;
import net.ravik_cms.ravik_backend.income.dtos.UpdateIncomeDto;
import net.ravik_cms.ravik_backend.income.entity.Income;
import net.ravik_cms.ravik_backend.income.mapper.IncomeMapper;
import net.ravik_cms.ravik_backend.income.repository.IncomeRepository;
import net.ravik_cms.ravik_backend.projects.Projects;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final ProjectsRepository projectsRepository;
    private final AccountRepository accountRepository;
    private final IncomeMapper incomeMapper;

    public void addIncome(CreateIncomeDto request, UUID projectId) {
        Projects project = projectsRepository.findById(projectId).orElseThrow(() ->
                new ResourceNotFoundException("Project not found"));
        Accounts account = accountRepository.findById(request.getAccountId()).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));
        Income income = incomeMapper.toEntity(request);
        income.setProject(project);
        income.setAccount(account);
        incomeRepository.save(income);
    }

    public Page<IncomeInfoProjection> getIncomes(UUID projectId, String search, Pageable pageable) {
        return incomeRepository.findAllByProject(projectId, search, pageable);
    }

    public void updateIncome(UUID id, UpdateIncomeDto request) {
        Income income = incomeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Income not found"));
        incomeMapper.updateIncome(request, income);
        if (request.getAccountId() != null) {
            Accounts account = accountRepository.findById(request.getAccountId()).orElseThrow(() ->
                    new ResourceNotFoundException("Account not found"));
            income.setAccount(account);
        }
        incomeRepository.save(income);
    }

    public void deleteIncome(UUID id) {
        Income income = incomeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Income not found"));
        incomeRepository.delete(income);
    }
}
