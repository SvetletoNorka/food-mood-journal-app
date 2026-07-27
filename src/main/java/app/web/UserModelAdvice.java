package app.web;

import app.model.entity.user.UserRole;
import app.security.AuthenticationMetadata;
import app.security.AuthenticationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UserModelAdvice {

    @ModelAttribute("userRole")
    public UserRole userRole() {
        if (!AuthenticationUtils.isAuthenticated()) {
            return null;
        }
        return AuthenticationUtils.getCurrentUser().getRole();
    }

    @ModelAttribute("currentUsername")
    public String currentUsername() {
        if (!AuthenticationUtils.isAuthenticated()) {
            return null;
        }
        AuthenticationMetadata user = AuthenticationUtils.getCurrentUser();
        return user.getUsername();
    }
}
