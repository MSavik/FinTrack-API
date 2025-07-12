package com.fintrack.fintrack_api.dto.request;

import com.fintrack.fintrack_api.model.Account;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequestDTO(
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    String name,

    Account.AccountType type,

    @Size(min = 3, max = 3, message = "Currency code must have exactly 3 characters")
    @Pattern(regexp = "^[A-Z]*$", message = "Currency code must contain only uppercase letters")
    String currency
) {
}
