package com.fintrack.fintrack_api.dto.request;

import com.fintrack.fintrack_api.model.Account;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record AccountFilterRequestDTO(
        String email,
        Account.AccountType type,
        Account.AccountStatus status,
        @Min(0)
        Integer page,
        @Max(100)
        Integer size
) {
    public AccountFilterRequestDTO {
        if (page == null || page < 0) page = 0;
        if (size == null || size > 20) size = 20;
    }
}
