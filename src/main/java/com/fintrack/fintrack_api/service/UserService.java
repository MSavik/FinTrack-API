package com.fintrack.fintrack_api.service;

import com.fintrack.fintrack_api.dto.request.UpdateProfileRequestDTO;
import com.fintrack.fintrack_api.dto.response.AdminUserProfileResponseDTO;
import com.fintrack.fintrack_api.dto.response.UserProfileResponseDTO;
import com.fintrack.fintrack_api.exception.InvalidRequestException;
import com.fintrack.fintrack_api.exception.UserAlreadyExistsException;
import com.fintrack.fintrack_api.exception.UserNotFoundException;
import com.fintrack.fintrack_api.mapper.UserMapper;
import com.fintrack.fintrack_api.model.Users;
import com.fintrack.fintrack_api.repository.UserRepository;
import com.fintrack.fintrack_api.util.ErrorMessages;
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
        validateUser(user);
        checkForExistingUser(user.getEmail(), user.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<Users> findByEmail(String email) {
        validateEmail(email);
        return userRepository.findByEmail(email);
    }

    public UserProfileResponseDTO getCurrentUserProfile(Users user) {
        validateUser(user);
        return userMapper.toUserProfileResponseDTO(user);
    }

    public UserProfileResponseDTO updateCurrentUserProfile(Users user, UpdateProfileRequestDTO request) {
        validateUser(user);
        if (request == null) {
            throw new InvalidRequestException(ErrorMessages.INVALID_REQUEST_BODY);
        }
        validateName(request.firstName(), ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH);
        validateName(request.lastName(), ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH);
        validatePhoneNumber(request.phoneNumber());
        checkForExistingUser(null, request.phoneNumber());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());

        user = userRepository.save(user);
        return userMapper.toUserProfileResponseDTO(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public AdminUserProfileResponseDTO getUserById(Long id) {
        if (id == null) {
            throw new InvalidRequestException(ErrorMessages.NULL_ID);
        }
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        return userMapper.toAdminUserProfileResponseDTO(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public void deactivateUser(Long id) {
        if (id == null) {
            throw new InvalidRequestException(ErrorMessages.NULL_ID);
        }
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));
        user.setEnabled(false);
        userRepository.save(user);
    }

    private void validateUser(Users user) {
        if (user == null) {
            throw new InvalidRequestException(ErrorMessages.NULL_USER);
        }
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validateName(user.getFirstName(), ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH);
        validateName(user.getLastName(), ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH);
        validatePhoneNumber(user.getPhoneNumber());
    }

    private void validateEmail(String email) {
        if (email == null || !email.matches(".+@.+\\..+")) {
            throw new InvalidRequestException(ErrorMessages.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128) {
            throw new InvalidRequestException(ErrorMessages.FORBIDDEN_PASSWORD_LENGTH);
        }
    }

    private void validateName(String name, String errorMessage) {
        if (name == null || name.length() < 2 || name.length() > 50) {
            throw new InvalidRequestException(errorMessage);
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !phoneNumber.matches("^\\+[1-9]\\d{6,14}$")) {
            throw new InvalidRequestException(ErrorMessages.INVALID_PHONE_NUMBER);
        }
    }

    private void checkForExistingUser(String email, String phoneNumber) {
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException(ErrorMessages.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new UserAlreadyExistsException(ErrorMessages.DUPLICATE_PHONE_NUMBER);
        }
    }
}
