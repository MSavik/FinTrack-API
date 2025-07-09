package com.fintrack.fintrack_api.dto.response;

import java.util.List;

public record AdminUserProfileResponseDTO(
        UserProfileResponseDTO profile,
        List<String> roles,
        boolean enabled
) {
}
