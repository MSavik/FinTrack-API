package com.fintrack.fintrack_api.dto.response;

import java.util.List;

public record JwtResponseDTO(
        String token,
        String type,
        String email,
        List<String> roles
) {
}
