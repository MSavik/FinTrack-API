package com.fintrack.fintrack_api.dto.request;

public record LoginRequestDTO(
        String email,
        String password
) {
}
