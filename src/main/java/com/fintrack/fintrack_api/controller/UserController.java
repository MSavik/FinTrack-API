package com.fintrack.fintrack_api.controller;

import com.fintrack.fintrack_api.dto.request.UserRegistrationRequestDTO;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequestDTO request) {
        Users user = new Users();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setName(request.name());
        user.setEnabled(true);
        user.setRoles(List.of("ROLE_USER"));

        Users savedUser = userService.registerUser(user);
        return ResponseEntity.ok(savedUser);
    }
}