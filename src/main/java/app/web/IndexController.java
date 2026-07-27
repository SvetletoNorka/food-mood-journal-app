package app.web;

import app.exception.DomainException;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserRegisterRequest;
import app.security.AuthenticationUtils;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        if (AuthenticationUtils.isAuthenticated()) {
            return "redirect:/home";
        }
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(value = "error", required = false) String error,
                                     @RequestParam(value = "disabled", required = false) String disabled) {
        if (AuthenticationUtils.isAuthenticated()) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView("login");
        if (disabled != null) {
            modelAndView.addObject("errorMessage", "Your account is deactivated. Contact an administrator.");
        } else if (error != null) {
            modelAndView.addObject("errorMessage", "Invalid username or password.");
        }
        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        if (AuthenticationUtils.isAuthenticated()) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView("register");
        modelAndView.addObject("userRegisterRequest", UserRegisterRequest.builder().build());
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView registerUser(@Valid @ModelAttribute("userRegisterRequest") UserRegisterRequest userRegisterRequest,
                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        try {
            userService.register(userRegisterRequest);
            return new ModelAndView("redirect:/login");
        } catch (DomainException ex) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        UserDto user = userService.findById(AuthenticationUtils.getCurrentUserId());

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("activePage", "home");
        return modelAndView;
    }

    @GetMapping("/access-denied")
    public ModelAndView accessDenied() {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", "You do not have permission to access this resource.");
        return modelAndView;
    }
}
