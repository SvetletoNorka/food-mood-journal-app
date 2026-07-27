package app.web.user;

import app.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ModelAndView getAllUsers() {
        ModelAndView modelAndView = new ModelAndView("users");
        modelAndView.addObject("users", userService.getAllUsers());
        modelAndView.addObject("activePage", "users");
        return modelAndView;
    }

    @PutMapping("/{id}/status")
    public ModelAndView switchUserStatus(@PathVariable String id) {
        userService.switchStatus(UUID.fromString(id));
        return new ModelAndView("redirect:/users");
    }

    @PutMapping("/{id}/role")
    public ModelAndView switchUserRole(@PathVariable String id) {
        userService.switchRole(UUID.fromString(id));
        return new ModelAndView("redirect:/users");
    }
}
