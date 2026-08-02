package app.service.user;

import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static app.util.user.UserFactory.getUserRegisterRequest;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
class UserServiceItTest {

    @Autowired
    private UserService underTest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_shouldPersistUser_withEncodedPasswordAndDefaultRole() {
        UserRegisterRequest request = getUserRegisterRequest();

        UserDto registered = underTest.register(request);

        User saved = userRepository.findById(registered.getId()).orElseThrow();
        assertEquals(request.getUsername(), saved.getUsername());
        assertEquals(request.getEmail(), saved.getEmail());
        assertEquals(UserRole.USER, saved.getRole());
        assertTrue(saved.isActive());
        assertTrue(passwordEncoder.matches("Password", saved.getPassword()));
    }

    @Test
    void updateProfileAndSwitchRole_shouldPersistChanges() {
        UserDto registered = underTest.register(getUserRegisterRequest());

        underTest.updateProfile(registered.getId(), EditProfileRequest.builder()
                .email("updated@example.com")
                .newPassword("newPass1")
                .confirmPassword("newPass1")
                .build());
        underTest.switchRole(registered.getId());
        underTest.switchStatus(registered.getId());

        User updated = userRepository.findById(registered.getId()).orElseThrow();
        assertEquals("updated@example.com", updated.getEmail());
        assertEquals(UserRole.ADMIN, updated.getRole());
        assertFalse(updated.isActive());
        assertTrue(passwordEncoder.matches("newPass1", updated.getPassword()));
    }
}
