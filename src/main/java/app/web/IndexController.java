package app.web;

import app.config.UserSession;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    private final UserService userService;
    private final UserSession userSession;

    public IndexController(UserService userService, UserSession userSession) {
        this.userService = userService;
        this.userSession = userSession;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        if (userSession.isLoggedIn()) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("userLoginRequest", UserLoginRequest.builder().build());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("userLoginRequest") UserLoginRequest userLoginRequest,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        try {
            UserDto user = userService.login(userLoginRequest);
            userSession.login(user.getId(), user.getUsername());
            return new ModelAndView("redirect:/home");
        } catch (RuntimeException ex) {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        if (userSession.isLoggedIn()) {
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
        } catch (RuntimeException ex) {
            ModelAndView modelAndView = new ModelAndView("register");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        ModelAndView redirect = SessionGuard.redirectToLoginIfNeeded(userSession);
        if (redirect != null) {
            return redirect;
        }

        UserDto user = userService.findById(userSession.getId());
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("activePage", "home");
        return modelAndView;
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        userSession.logout();
        return new ModelAndView("redirect:/");
    }
}
