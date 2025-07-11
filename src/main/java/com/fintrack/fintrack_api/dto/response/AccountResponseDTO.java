package com.fintrack.fintrack_api.dto.response;

import com.fintrack.fintrack_api.model.Account;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record AccountResponseDTO(
        Long id,
        String accountNumber,
        String name,
        Account.AccountType type,
        BigDecimal balance,
        String currency,
        Account.AccountStatus status,
        String userEmail,
        LocalDateTime createdAt
) {
}
