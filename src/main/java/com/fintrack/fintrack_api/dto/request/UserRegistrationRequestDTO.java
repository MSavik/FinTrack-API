package com.fintrack.fintrack_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDTO(
    @Email String email,
    @Size(min=8) String password,
    @NotBlank String name
) {
}
