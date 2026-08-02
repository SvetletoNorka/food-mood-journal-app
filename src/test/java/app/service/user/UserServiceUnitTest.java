package app.service.user;

import app.exception.DuplicateEmailException;
import app.exception.DuplicateUsernameException;
import app.exception.PasswordMismatchException;
import app.exception.UserNotFoundException;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.util.user.UserFactory.getUserEntity;
import static app.util.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService underTest;

    @Test
    void register_shouldSaveUser_whenUsernameAndEmailAreUnique() {
        UserRegisterRequest request = getUserRegisterRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });

        UserDto result = underTest.register(request);

        assertEquals("User123", result.getUsername());
        assertEquals("user123@example.com", result.getEmail());
        assertEquals(UserRole.USER, result.getRole());
        assertTrue(result.isActive());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenUsernameExists() {
        UserRegisterRequest request = getUserRegisterRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(getUserEntity()));

        assertThrows(DuplicateUsernameException.class, () -> underTest.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrow_whenEmailExists() {
        UserRegisterRequest request = getUserRegisterRequest();
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(getUserEntity()));

        assertThrows(DuplicateEmailException.class, () -> underTest.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_shouldReturnDto_whenUserExists() {
        User user = getUserEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = underTest.findById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void findById_shouldThrow_whenUserMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> underTest.findById(id));
    }

    @Test
    void getAllUsers_shouldMapAllUsers() {
        User user = getUserEntity();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = underTest.getAllUsers();

        assertEquals(1, result.size());
        assertEquals(user.getUsername(), result.get(0).getUsername());
    }

    @Test
    void updateProfile_shouldUpdateEmailAndPassword() {
        User user = getUserEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNew");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EditProfileRequest request = EditProfileRequest.builder()
                .email("new@example.com")
                .newPassword("newPass")
                .confirmPassword("newPass")
                .build();

        UserDto result = underTest.updateProfile(user.getId(), request);

        assertEquals("new@example.com", result.getEmail());
        assertEquals("encodedNew", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_shouldThrow_whenPasswordsDoNotMatch() {
        User user = getUserEntity();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        EditProfileRequest request = EditProfileRequest.builder()
                .email(user.getEmail())
                .newPassword("newPass")
                .confirmPassword("other")
                .build();

        assertThrows(PasswordMismatchException.class, () -> underTest.updateProfile(user.getId(), request));
    }

    @Test
    void updateProfile_shouldThrow_whenEmailAlreadyTaken() {
        User user = getUserEntity();
        User other = getUserEntity();
        other.setEmail("taken@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(other));

        EditProfileRequest request = EditProfileRequest.builder()
                .email("taken@example.com")
                .build();

        assertThrows(DuplicateEmailException.class, () -> underTest.updateProfile(user.getId(), request));
    }

    @Test
    void switchStatus_shouldToggleActiveFlag() {
        User user = getUserEntity();
        user.setActive(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        underTest.switchStatus(user.getId());

        assertFalse(user.isActive());
        assertNotNull(user.getUpdatedOn());
        verify(userRepository).save(user);
    }

    @Test
    void switchRole_shouldToggleBetweenUserAndAdmin() {
        User user = getUserEntity();
        user.setRole(UserRole.USER);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        underTest.switchRole(user.getId());
        assertEquals(UserRole.ADMIN, user.getRole());

        underTest.switchRole(user.getId());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void switchStatus_shouldThrow_whenUserMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> underTest.switchStatus(id));
    }
}
