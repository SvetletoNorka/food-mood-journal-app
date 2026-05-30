package app.model.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginRequest {

    @NotBlank
    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
