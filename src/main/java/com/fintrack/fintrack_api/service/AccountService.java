package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.dto.request.AccountFilterRequestDTO;
import com.fintrack.fintrack_api.dto.request.CreateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.request.UpdateAccountRequestDTO;
import com.fintrack.fintrack_api.dto.response.AccountResponseDTO;
import com.fintrack.fintrack_api.exception.AccountNotFoundException;
import com.fintrack.fintrack_api.exception.InvalidAccountOperationException;
import com.fintrack.fintrack_api.mapper.AccountMapper;
import com.fintrack.fintrack_api.model.Account;
import com.fintrack.fintrack_api.model.Users;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final AccountNumberGeneratorService accountNumberGeneratorService;

    public List<AccountResponseDTO> getAllActiveUserAccounts(UserPrincipal currentUser) {
        List<Account> accounts = accountRepository.findActiveAccountsByUser(currentUser.getUser());
        return accounts.stream()
                .map(accountMapper::toAccountResponseDTO)
                .collect(Collectors.toList());
    }

    public AccountResponseDTO getActiveAccountById(Long accountId, UserPrincipal currentUser) {
        Account account = accountRepository.findActiveAccountByIdAndUser(
                        accountId,
                        currentUser.getUser())
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
        return accountMapper.toAccountResponseDTO(account);
    }

    public AccountResponseDTO getActiveAccountByAccountNumber(String accountNumber, UserPrincipal currentUser) {
        Account account = accountRepository.findActiveAccountByAccountNumberAndUser(
                        accountNumber,
                        currentUser.getUser())
                .orElseThrow(() -> new AccountNotFoundException("Account with account number " + accountNumber + " not found"));
        return accountMapper.toAccountResponseDTO(account);
    }

    @Transactional
    public AccountResponseDTO createAccount(CreateAccountRequestDTO accountRequest, UserPrincipal currentUser) {
        Account account = accountMapper.toAccount(accountRequest);
        validateAccountBalance(account);

        account.setUser(currentUser.getUser());
        account.setAccountNumber(accountNumberGeneratorService.generateAccountNumber(accountRequest.type()));
        return accountMapper.toAccountResponseDTO(
                accountRepository.save(account));
    }

    @Transactional
    public AccountResponseDTO updateAccountById(Long accountId, UpdateAccountRequestDTO accountUpdate, UserPrincipal currentUser) {
        Users user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentUser.getUsername()));
        Account existingAccount = accountRepository.findActiveAccountByIdAndUser(accountId, user)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
        existingAccount.setName(accountUpdate.name());

        return accountMapper.toAccountResponseDTO(
                accountRepository.save(existingAccount));
    }

    @Transactional
    public AccountResponseDTO updateAccountByAccountNumber(String accountNumber, UpdateAccountRequestDTO accountUpdate, UserPrincipal currentUser) {
        Users user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentUser.getUsername()));
        Account existingAccount = accountRepository.findActiveAccountByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new AccountNotFoundException("Account with account number " + accountNumber + " not found"));
        existingAccount.setName(accountUpdate.name());

        return accountMapper.toAccountResponseDTO(
                accountRepository.save(existingAccount));
    }

    @Transactional
    public void deactivateAccountById(Long accountId, UserPrincipal currentUser) {
        Users user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentUser.getUsername()));
        Account account = accountRepository.findActiveAccountByIdAndUser(accountId, user)
                .orElseThrow(() -> new AccountNotFoundException("Account with ID " + accountId + " not found"));
        account.setStatus(Account.AccountStatus.INACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void deactivateAccountByAccountNumber(String accountNumber, UserPrincipal currentUser) {
        Users user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + currentUser.getUsername()));
        Account account = accountRepository.findActiveAccountByAccountNumberAndUser(accountNumber, user)
                .orElseThrow(() -> new AccountNotFoundException("Account with account number " + accountNumber + " not found"));
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
                filter.accountNumber(),
                pageable
        ).map(accountMapper::toAccountResponseDTO);
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
}
