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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void testRegisterUser_Success() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        Users savedUser = Users.builder()
                .id(user.getId())
                .build();
        String passwordBeforeEncoding = user.getPassword();
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(savedUser);

        // Act
        Users result = userService.registerUser(user);

        // Assert
        assertNotNull(result.getId());
        verify(passwordEncoder).encode(passwordBeforeEncoding);
        verify(userRepository).save(argThat(u ->
                u.getPassword().equals(encodedPassword) && u.getEmail().equals(user.getEmail())
        ));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        // Arrange
        Users duplicateUser = UserMockedObjects.mockUser();

        when(userRepository.existsByEmail(duplicateUser.getEmail())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(duplicateUser)
        );

        assertEquals(ErrorMessages.DUPLICATE_EMAIL, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_DuplicatePhoneNumber() {
        // Arrange
        Users duplicateUser = UserMockedObjects.mockUser();

        when(userRepository.existsByPhoneNumber(duplicateUser.getPhoneNumber())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.registerUser(duplicateUser)
        );

        assertEquals(ErrorMessages.DUPLICATE_PHONE_NUMBER, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterUser_InvalidEmail() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setEmail("invalid-email");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
    }

    @Test
    void testRegisterUser_NullEmail() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setEmail(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
    }

    @Test
    void testRegisterUser_EmptyEmail() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setEmail("");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
    }

    @Test
    void testRegisterUser_PasswordTooShort() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPassword("short");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_PASSWORD_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_PasswordTooLong() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        user.setPassword("a".repeat(129));

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_PASSWORD_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_NullPassword() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPassword(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_PASSWORD_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_NullFirstName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setFirstName(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_EmptyFirstName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setFirstName("");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_FirstNameTooShort() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setFirstName("A");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_FirstNameTooLong() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setFirstName("a".repeat(51));

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_NullLastName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setLastName(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_EmptyLastName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setLastName(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_LastNameTooShort() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setLastName("A");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_LastNameTooLong() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setLastName("a".repeat(51));

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH, exception.getMessage());
    }

    @Test
    void testRegisterUser_NullPhoneNumber() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber(null);

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_EmptyPhoneNumber() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_PhoneNumberTooShort() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("+111111");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_PhoneNumberTooLong() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("+1111111111111111");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_PhoneNumberMissingPlus() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("1111111111");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_PhoneNumberForbiddenCharacters() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("+11V11a1m11");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testRegisterUser_PhoneNumberSpecialCharacters() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setPhoneNumber("++11#11*11!1");

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.registerUser(user)
        );

        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        Optional<Users> result = userService.findByEmail(Objects.requireNonNull(user).getEmail());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void testFindByEmail_UserDoesNotExist() {
        // Arrange
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Optional<Users> result = userService.findByEmail(email);

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_NullInput() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.findByEmail(null)
        );
        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findByEmail_EmptyStringInput() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.findByEmail("")
        );
        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findByEmail_InvalidEmail() {
        // Arrange
        String invalidEmail = "not-an-email";

        // Act & Assert
        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> userService.findByEmail(invalidEmail)
        );
        assertEquals(ErrorMessages.INVALID_EMAIL_FORMAT, exception.getMessage());
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void findByEmail_VerifyRepositoryInteraction() {
        // Arrange
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        userService.findByEmail(email);

        // Assert
        verify(userRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getCurrentUserProfile_Success() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        UserProfileResponseDTO expectedDto = new UserProfileResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

        when(userMapper.toUserProfileResponseDTO(user)).thenReturn(expectedDto);

        // Act
        UserProfileResponseDTO result = userService.getCurrentUserProfile(user);

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.id());
        assertEquals(user.getEmail(), result.email());
        verify(userMapper, times(1)).toUserProfileResponseDTO(user);
    }

    @Test
    void getCurrentUserProfile_NullInput() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.getCurrentUserProfile(null));
        assertEquals(ErrorMessages.NULL_USER, exception.getMessage());
        verifyNoInteractions(userMapper);
    }

    @Test
    void getCurrentUserProfile_VerifyMapperInteraction() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        when(userMapper.toUserProfileResponseDTO(user))
                .thenReturn(new UserProfileResponseDTO(
                        user.getId(),
                        user.getPassword(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt(),
                        user.getUpdatedAt())
                );

        // Act
        userService.getCurrentUserProfile(user);

        // Assert
        verify(userMapper, times(1)).toUserProfileResponseDTO(user);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_Success() {
        // Arrange
        Users existingUser = UserMockedObjects.mockUser();

        String newFirstName = "NewFirst";
        String newLastName = "NewLast";
        String newPhoneNumber = "+9876543210";

        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                newFirstName,
                newLastName,
                newPhoneNumber
        );

        Users savedUser = Users.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail())
                .password(existingUser.getPassword())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phoneNumber(request.phoneNumber())
                .roles(existingUser.getRoles())
                .enabled(existingUser.isEnabled())
                .createdAt(existingUser.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        UserProfileResponseDTO expectedResponse = new UserProfileResponseDTO(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getPhoneNumber(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );

        when(userRepository.save(any(Users.class))).thenReturn(savedUser);
        when(userMapper.toUserProfileResponseDTO(savedUser)).thenReturn(expectedResponse);

        // Act
        UserProfileResponseDTO result = userService.updateCurrentUserProfile(existingUser, request);

        // Assert
        assertEquals(request.firstName(), result.firstName());
        assertEquals(request.lastName(), result.lastName());
        assertEquals(request.phoneNumber(), result.phoneNumber());
        verify(userRepository, times(1)).save(existingUser);
        verify(userMapper, times(1)).toUserProfileResponseDTO(savedUser);
    }

    @Test
    void updateCurrentUserProfile_NullUser() {
        // Arrange
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "Valid", "Valid", "+1234567890");

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.updateCurrentUserProfile(null, request));
        assertEquals(ErrorMessages.NULL_USER, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_NullRequest() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.updateCurrentUserProfile(user, null));
        assertEquals(ErrorMessages.INVALID_REQUEST_BODY, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_InvalidFirstName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "A", // Too short
                "Valid",
                "+1234567890");

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.updateCurrentUserProfile(user, request));
        assertEquals(ErrorMessages.FORBIDDEN_USER_FIRST_NAME_LENGTH, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_InvalidLastName() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "Valid",
                "L", // Too short
                "+1234567890");

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.updateCurrentUserProfile(user, request));
        assertEquals(ErrorMessages.FORBIDDEN_USER_LAST_NAME_LENGTH, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_InvalidPhoneNumber() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "Valid",
                "Valid",
                "1234567890"); // Missing + prefix

        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> userService.updateCurrentUserProfile(user, request));
        assertEquals(ErrorMessages.INVALID_PHONE_NUMBER, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateCurrentUserProfile_DuplicatePhoneNumber() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO(
                "Valid",
                "Valid",
                "+123454321");

        when(userRepository.existsByPhoneNumber(request.phoneNumber())).thenReturn(true);

        // Act & Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateCurrentUserProfile(user, request));
        assertEquals(ErrorMessages.DUPLICATE_PHONE_NUMBER, exception.getMessage());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserById_Success() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        AdminUserProfileResponseDTO expectedResponse = new AdminUserProfileResponseDTO(
                new UserProfileResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()),
                user.getRoles(),
                user.isEnabled()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toAdminUserProfileResponseDTO(user)).thenReturn(expectedResponse);

        // Act
        AdminUserProfileResponseDTO result = userService.getUserById(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.profile().id());
        assertEquals(user.getRoles(), result.roles());
        assertEquals(user.isEnabled(), result.enabled());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toAdminUserProfileResponseDTO(user);
    }

    @Test
    void getUserById_AdminUser() {
        // Arrange
        Users user = UserMockedObjects.mockAdminUser();

        AdminUserProfileResponseDTO expectedResponse = new AdminUserProfileResponseDTO(
                new UserProfileResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()),
                user.getRoles(),
                user.isEnabled()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toAdminUserProfileResponseDTO(user)).thenReturn(expectedResponse);

        // Act
        AdminUserProfileResponseDTO result = userService.getUserById(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.profile().id());
        assertEquals(user.getRoles(), result.roles());
        assertEquals(user.isEnabled(), result.enabled());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toAdminUserProfileResponseDTO(user);
    }

    @Test
    void getUserById_DisabledUser() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        user.setEnabled(false);

        AdminUserProfileResponseDTO expectedResponse = new AdminUserProfileResponseDTO(
                new UserProfileResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhoneNumber(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()),
                user.getRoles(),
                user.isEnabled()
        );

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toAdminUserProfileResponseDTO(user)).thenReturn(expectedResponse);

        // Act
        AdminUserProfileResponseDTO result = userService.getUserById(user.getId());

        // Assert
        assertNotNull(result);
        assertEquals(user.getId(), result.profile().id());
        assertEquals(user.getRoles(), result.roles());
        assertEquals(user.isEnabled(), result.enabled());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userMapper, times(1)).toAdminUserProfileResponseDTO(user);
    }

    @Test
    void getUserById_UserDoesNotExist() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        assertEquals(ErrorMessages.USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserById_NullInput() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.getUserById(null));
        assertEquals(ErrorMessages.NULL_ID, exception.getMessage());
        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserById_VerifyRepositoryInteraction() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toAdminUserProfileResponseDTO(user)).thenReturn(any());

        // Act
        userService.getUserById(user.getId());

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserById_VerifyMapperInteraction() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toAdminUserProfileResponseDTO(user)).thenReturn(any());

        // Act
        userService.getUserById(user.getId());

        // Assert
        verify(userMapper, times(1)).toAdminUserProfileResponseDTO(user);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        Users user = UserMockedObjects.mockUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        userService.deactivateUser(user.getId());

        // Assert
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository).save(userCaptor.capture());

        assertFalse(userCaptor.getValue().isEnabled());
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void deactivateUser_UserDoesNotExist() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deactivateUser(userId));
        assertEquals(ErrorMessages.USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deactivateUser_NullInput() {
        // Act & Assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.deactivateUser(null));
        assertEquals(ErrorMessages.NULL_ID, exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void deactivateUser_VerifyRepositoryInteractions() {
        // Arrange
        Users user = UserMockedObjects.mockUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        userService.deactivateUser(user.getId());

        // Assert
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deactivateUser_AlreadyDeactivated_StillSaves() {
        // Arrange
        Users inactiveUser = UserMockedObjects.mockUser();
        inactiveUser.setEnabled(false);

        when(userRepository.findById(inactiveUser.getId())).thenReturn(Optional.of(inactiveUser));

        // Act
        userService.deactivateUser(inactiveUser.getId());

        // Assert
        verify(userRepository).save(inactiveUser);
        assertFalse(inactiveUser.isEnabled());
    }
}
