package net.ravik_cms.ravik_backend.account.service;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.dtos.AccountInfoProjection;
import net.ravik_cms.ravik_backend.account.dtos.CreateAccountDto;
import net.ravik_cms.ravik_backend.account.dtos.UpdateAccountDto;
import net.ravik_cms.ravik_backend.account.entity.Accounts;
import net.ravik_cms.ravik_backend.account.mapper.AccountMapper;
import net.ravik_cms.ravik_backend.account.repository.AccountRepository;
import net.ravik_cms.ravik_backend.client.Client;
import net.ravik_cms.ravik_backend.client.ClientRepository;
import net.ravik_cms.ravik_backend.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;
    private final AccountMapper accountMapper;

    public void addAccount(CreateAccountDto request, UUID clientId) {
        Client client = clientRepository.findById(clientId).orElseThrow(() ->
                new ResourceNotFoundException("Client not found"));
        Accounts account = accountMapper.toEntity(request);
        account.setClient(client);
        accountRepository.save(account);
    }

    public Page<AccountInfoProjection> getAccounts(UUID clientId, String search, Pageable pageable) {
        return accountRepository.findAllByClient(clientId, search, pageable);
    }

    public void updateAccount(UUID id, UpdateAccountDto request) {
        Accounts account = accountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));
        accountMapper.updateAccount(request, account);
        accountRepository.save(account);
    }

    public void deleteAccount(UUID id) {
        Accounts account = accountRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Account not found"));
        accountRepository.delete(account);
    }
}
