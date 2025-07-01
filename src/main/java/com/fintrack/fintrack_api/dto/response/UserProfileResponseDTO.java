package com.fintrack.fintrack_api.dto.response;

import java.time.LocalDateTime;

public record UserProfileResponseDTO(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
