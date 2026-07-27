package app.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileRequest {

    @NotBlank
    @Email
    private String email;

    @Size(min = 4, max = 40, message = "Password must be between 4 and 40 characters")
    private String newPassword;

    private String confirmPassword;
}
