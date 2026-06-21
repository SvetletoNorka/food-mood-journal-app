package app.web;

import app.model.entity.user.UserRole;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.UUID;

@ControllerAdvice
public class UserModelAdvice {

    private final UserService userService;

    public UserModelAdvice(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("userRole")
    public UserRole userRole(HttpSession session) {
        if (session == null) {
            return null;
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            return null;
        }

        return userService.findById(userId).getRole();
    }
}
