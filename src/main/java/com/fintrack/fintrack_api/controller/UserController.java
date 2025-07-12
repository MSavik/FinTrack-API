package com.fintrack.fintrack_api.controller;

import com.fintrack.fintrack_api.dto.request.UpdateProfileRequestDTO;
import com.fintrack.fintrack_api.dto.request.UserRegistrationRequestDTO;
import com.fintrack.fintrack_api.dto.response.AdminUserProfileResponseDTO;
import com.fintrack.fintrack_api.dto.response.UserProfileResponseDTO;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.model.enums.Role;
import com.fintrack.fintrack_api.service.AuthenticationService;
import com.fintrack.fintrack_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "APIs for user management")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Register user profile")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequestDTO request) {
        Users user = new Users();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setEnabled(true);
        user.setRoles(List.of(Role.USER.getValue()));

        Users savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @Operation(summary = "Get current user profile")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile() {
        Users currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(userService.getCurrentUserProfile(currentUser));
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        Users currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(userService.updateCurrentUserProfile(currentUser, request));
    }

    @Operation(summary = "Get user profile by user ID (ADMIN only)")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminUserProfileResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Deactivate user profile by user ID (ADMIN only)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
