package app.web;

import app.config.UserSession;
import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.service.user.UserService;
import org.springframework.stereotype.Controller;
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
        modelAndView.addObject("userLoginData", UserLoginRequest.builder().build());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@ModelAttribute UserLoginRequest userLoginRequest) {
        UserDto user = userService.login(userLoginRequest);
        userSession.login(user.getId(), user.getUsername());

        return new ModelAndView("redirect:/home");
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
    public ModelAndView registerUser(@ModelAttribute UserRegisterRequest userRegisterRequest) {
        userService.register(userRegisterRequest);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage() {
        if (!userSession.isLoggedIn()) {
            return new ModelAndView("redirect:/login");
        }

        UserDto user = userService.findById(userSession.getId());
        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/logout")
    public ModelAndView logout() {
        userSession.logout();
        return new ModelAndView("redirect:/");
    }
}
