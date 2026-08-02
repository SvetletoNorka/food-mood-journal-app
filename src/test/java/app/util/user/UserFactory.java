package app.util.user;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;
import app.security.AuthenticationMetadata;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserFactory {

    public static UserRegisterRequest getUserRegisterRequest() {
        return UserRegisterRequest.builder()
                .username("User123")
                .email("user123@example.com")
                .password("Password")
                .build();
    }

    public static UserDto getUserDto() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        return UserDto.builder()
                .id(id)
                .username("User123")
                .email("user123@example.com")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(now)
                .updatedOn(now)
                .build();
    }

    public static User getUserEntity() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .id(id)
                .username("User123")
                .email("user123@example.com")
                .password("encoded")
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(now)
                .updatedOn(now)
                .build();
    }

    public static AuthenticationMetadata getUserPrincipal() {
        return new AuthenticationMetadata(
                UUID.randomUUID(),
                "User123",
                "encoded",
                UserRole.USER,
                true
        );
    }

    public static AuthenticationMetadata getAdminUser() {
        return new AuthenticationMetadata(
                UUID.randomUUID(),
                "AdminUser",
                "encoded",
                UserRole.ADMIN,
                true
        );
    }
}
