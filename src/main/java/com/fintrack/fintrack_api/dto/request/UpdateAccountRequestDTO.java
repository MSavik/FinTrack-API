package com.fintrack.fintrack_api.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateAccountRequestDTO(
        @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
        String name
) {
}
