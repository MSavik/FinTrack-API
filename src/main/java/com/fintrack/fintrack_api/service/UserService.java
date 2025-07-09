package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.dto.request.UpdateProfileRequestDTO;
import com.fintrack.fintrack_api.dto.response.AdminUserProfileResponseDTO;
import com.fintrack.fintrack_api.dto.response.UserProfileResponseDTO;
import com.fintrack.fintrack_api.mapper.UserMapper;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public Users registerUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<Users> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserProfileResponseDTO getCurrentUserProfile(Users user) {
        return userMapper.toUserProfileResponseDTO(user);
    }

    public UserProfileResponseDTO updateCurrentUserProfile(Users user, UpdateProfileRequestDTO request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());

        user = userRepository.save(user);
        return userMapper.toUserProfileResponseDTO(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminUserProfileResponseDTO getUserById(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toAdminUserResponseDTO(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deactivateUser(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }
}
