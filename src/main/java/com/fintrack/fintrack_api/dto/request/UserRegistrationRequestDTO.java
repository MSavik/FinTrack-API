package com.fintrack.fintrack_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDTO(
    @Email String email,
    @Size(min = 8) String password,

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @NotBlank String firstName,

    @NotBlank
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    String lastName,

    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{6,14}$", message = "Phone number should be valid")
    String phoneNumber
) {
}
