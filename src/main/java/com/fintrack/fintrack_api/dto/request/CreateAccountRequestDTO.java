package com.fintrack.fintrack_api.dto.request;

import com.fintrack.fintrack_api.model.Account;

public record CreateAccountRequestDTO(
    String name,
    Account.AccountType type,
    String currency
) {
}
