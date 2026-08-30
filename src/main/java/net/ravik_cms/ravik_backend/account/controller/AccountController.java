package net.ravik_cms.ravik_backend.account.controller;

import lombok.RequiredArgsConstructor;
import net.ravik_cms.ravik_backend.account.dtos.AccountInfoProjection;
import net.ravik_cms.ravik_backend.account.dtos.CreateAccountDto;
import net.ravik_cms.ravik_backend.account.dtos.UpdateAccountDto;
import net.ravik_cms.ravik_backend.account.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/{client_id}")
    public ResponseEntity<?> addAccount(@PathVariable UUID client_id,
                                         @RequestBody CreateAccountDto request) {
        accountService.addAccount(request, client_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{client_id}")
    public ResponseEntity<Page<AccountInfoProjection>> getAccounts(
            @PathVariable UUID client_id,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<AccountInfoProjection> body = accountService.getAccounts(client_id, search, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable UUID id, @RequestBody UpdateAccountDto request) {
        accountService.updateAccount(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
