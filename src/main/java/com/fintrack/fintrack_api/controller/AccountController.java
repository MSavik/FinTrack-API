package com.fintrack.fintrack_api.controller;

import com.fintrack.fintrack_api.dto.request.AccountFilterRequestDTO;
import com.fintrack.fintrack_api.dto.request.CreateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.request.UpdateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.response.AccountResponseDTO;
import com.fintrack.fintrack_api.model.Account;
import com.fintrack.fintrack_api.security.UserPrincipal;
import com.fintrack.fintrack_api.service.AccountService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllActiveAccounts(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.getAllActiveUserAccounts(currentUser));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<AccountResponseDTO> getActiveAccountById(@PathVariable Long id,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.getActiveAccountById(id, currentUser));
    }

    @GetMapping("/accountNumber/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getActiveAccountByAccountNumber(@PathVariable String accountNumber,
                                                                   @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.getActiveAccountByAccountNumber(accountNumber, currentUser));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody CreateAccountRequestDTO accountRequest,
                                         @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.createAccount(accountRequest, currentUser));
    }

    @PutMapping("/id/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccountById(@PathVariable Long id,
                                            @RequestBody UpdateAccountRequestDTO accountRequest,
                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.updateAccountById(id, accountRequest, currentUser));
    }

    @PutMapping("/accountNumber/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> updateAccountByAccountNumber(@PathVariable String accountNumber,
                                                            @RequestBody UpdateAccountRequestDTO accountRequest,
                                                            @AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(accountService.updateAccountByAccountNumber(accountNumber, accountRequest, currentUser));
    }

    @DeleteMapping("/id/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccountById(@PathVariable Long id,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        accountService.deactivateAccountById(id, currentUser);
    }

    @DeleteMapping("/accountNumber/{accountNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivateAccountByAccountNumber(@PathVariable String accountNumber,
                                  @AuthenticationPrincipal UserPrincipal currentUser) {
        accountService.deactivateAccountByAccountNumber(accountNumber, currentUser);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<AccountResponseDTO>> getAccounts(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Account.AccountType type,
            @RequestParam(required = false) Account.AccountStatus status,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Max(100) int size) {

        AccountFilterRequestDTO filter = AccountFilterRequestDTO.builder()
                .email(email)
                .type(type)
                .status(status)
                .accountNumber(accountNumber)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(accountService.getAccountsByFilters(filter));
    }
}
