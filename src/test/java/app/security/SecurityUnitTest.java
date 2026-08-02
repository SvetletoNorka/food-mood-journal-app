package app.security;

import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.util.user.UserFactory.getUserEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void loadUserByUsername_shouldReturnAuthenticationMetadata() {
        User user = getUserEntity();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails details = customUserDetailsService.loadUserByUsername(user.getUsername());

        assertInstanceOf(AuthenticationMetadata.class, details);
        AuthenticationMetadata metadata = (AuthenticationMetadata) details;
        assertEquals(user.getId(), metadata.getUserId());
        assertEquals(user.getUsername(), metadata.getUsername());
        assertTrue(metadata.isEnabled());
        assertTrue(metadata.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(metadata.isAccountNonExpired());
        assertTrue(metadata.isAccountNonLocked());
        assertTrue(metadata.isCredentialsNonExpired());
    }

    @Test
    void loadUserByUsername_shouldThrow_whenMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing"));
    }

    @Test
    void authenticationUtils_shouldReadCurrentUserFromContext() {
        AuthenticationMetadata metadata = new AuthenticationMetadata(
                UUID.randomUUID(), "admin", "pwd", UserRole.ADMIN, true);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(metadata, null, metadata.getAuthorities()));

        assertTrue(AuthenticationUtils.isAuthenticated());
        assertEquals(metadata.getUserId(), AuthenticationUtils.getCurrentUserId());
        assertEquals("admin", AuthenticationUtils.getCurrentUser().getUsername());
    }

    @Test
    void authenticationUtils_shouldThrow_whenNoAuthenticatedUser() {
        SecurityContextHolder.clearContext();

        assertFalse(AuthenticationUtils.isAuthenticated());
        assertThrows(IllegalStateException.class, AuthenticationUtils::getCurrentUserId);
        assertThrows(IllegalStateException.class, AuthenticationUtils::getCurrentUser);
    }

    @Test
    void authenticationMetadata_shouldDisableInactiveUsers() {
        AuthenticationMetadata metadata = new AuthenticationMetadata(
                UUID.randomUUID(), "user", "pwd", UserRole.USER, false);

        assertFalse(metadata.isEnabled());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_USER")), metadata.getAuthorities());
        assertEquals("pwd", metadata.getPassword());
    }
}
