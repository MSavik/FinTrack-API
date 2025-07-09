package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.dto.request.AccountFilterRequestDTO;
import com.fintrack.fintrack_api.dto.request.CreateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.request.UpdateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.response.AccountResponseDTO;
import com.fintrack.fintrack_api.exception.AccountNotFoundException;
import com.fintrack.fintrack_api.exception.InvalidAccountOperationException;
import com.fintrack.fintrack_api.model.Account;
import com.fintrack.fintrack_api.repository.AccountRepository;
import com.fintrack.fintrack_api.repository.UserRepository;
import com.fintrack.fintrack_api.security.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<Account> getAllActiveUserAccounts(UserPrincipal currentUser) {
        return accountRepository.findActiveAccountsByUser(
                currentUser.getUser());
    }

    public Account getActiveAccountById(Long accountId, UserPrincipal currentUser) {
        return accountRepository.findActiveAccountByIdAndUser(
                        accountId,
                        currentUser.getUser())
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
    }

    @Transactional
    public Account createAccount(Account account, UserPrincipal currentUser) {
        validateAccountBalance(account);

        account.setUser(currentUser.getUser());
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateAccount(Long accountId, Account accountUpdate, UserPrincipal currentUser) {
        Account existingAccount = getActiveAccountById(accountId, currentUser);
        existingAccount.setName(accountUpdate.getName());

        return accountRepository.save(existingAccount);
    }

    @Transactional
    public void deactivateAccount(Long accountId, UserPrincipal currentUser) {
        Account account = getActiveAccountById(accountId, currentUser);
        account.setStatus(Account.AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public Page<AccountResponseDTO> getAccountsByFilters(AccountFilterRequestDTO filter) {
        if (StringUtils.hasText(filter.email())) {
            userRepository.findByEmail(filter.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + filter.email()));
        }

        Pageable pageable = PageRequest.of(
                filter.page(),
                filter.size(),
                Sort.by("createdAt").descending()
        );

        return accountRepository.findFilteredAccounts(
                StringUtils.hasText(filter.email()) ? filter.email() : null,
                filter.type(),
                filter.status(),
                pageable
        ).map(this::convertToResponse);
    }

    private void validateAccountBalance(Account account) {
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }

        if ((account.getType() == Account.AccountType.CHECKING ||
                account.getType() == Account.AccountType.SAVINGS) &&
                account.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAccountOperationException(
                    "Balance cannot be negative for CHECKING/SAVINGS accounts");
        }
    }

    public AccountResponseDTO convertToResponse(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .userEmail(account.getUser().getEmail())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public Account convertToEntity(CreateAccountRequestDTO accountRequest) {
        return Account.builder()
                .name(accountRequest.name())
                .type(accountRequest.type())
                .currency(accountRequest.currency())
                .build();
    }

    public Account convertToEntity(UpdateAccountRequestDTO accountRequest) {
        return Account.builder()
                .name(accountRequest.name())
                .build();
    }

}
