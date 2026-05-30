package app.mapper.user;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.model.entity.user.User;
import app.model.entity.user.UserRole;

import java.time.LocalDateTime;

public class UserMapper {

    private UserMapper() {
    }

    public static User toUserEntity(UserRegisterRequest request) {
        if (request == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(UserRole.USER)
                .createdOn(now)
                .updatedOn(now)
                .build();
    }

    public static UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdOn(user.getCreatedOn())
                .updatedOn(user.getUpdatedOn())
                .build();
    }
}
