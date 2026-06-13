package app.model.dto.user;

import app.model.entity.user.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDto {

    private UUID id;
    private String username;
    private String email;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
