package com.fintrack.fintrack_api.controller;

import com.fintrack.fintrack_api.dto.request.UpdateProfileRequestDTO;
import com.fintrack.fintrack_api.dto.request.UserRegistrationRequestDTO;
import com.fintrack.fintrack_api.dto.response.AdminUserProfileResponseDTO;
import com.fintrack.fintrack_api.dto.response.UserProfileResponseDTO;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.model.enums.Role;
import com.fintrack.fintrack_api.service.AuthenticationService;
import com.fintrack.fintrack_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequestDTO request) {
        Users user = new Users();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEnabled(true);
        user.setRoles(List.of(Role.USER.getValue()));

        Users savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> getCurrentUserProfile() {
        Users currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(userService.getCurrentUserProfile(currentUser));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponseDTO> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        Users currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(userService.updateCurrentUserProfile(currentUser, request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<AdminUserProfileResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
