package app.web;

import app.model.dto.user.UserDto;
import app.model.dto.user.UserLoginRequest;
import app.model.dto.user.UserRegisterRequest;
import app.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class IndexController {

    private final UserService userService;

    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(HttpSession httpSession) {
        if (isLoggedIn(httpSession)) {
            return new ModelAndView("redirect:/home");
        }

        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("userLoginRequest", UserLoginRequest.builder().build());
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute("userLoginRequest") UserLoginRequest userLoginRequest,
                              BindingResult bindingResult,
                              HttpSession httpSession) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("login");
        }

        try {
            UserDto user = userService.login(userLoginRequest);
            httpSession.setAttribute("user_id", user.getId());
            return new ModelAndView("redirect:/home");
        } catch (RuntimeException ex) {
            ModelAndView modelAndView = new ModelAndView("login");
            modelAndView.addObject("errorMessage", ex.getMessage());
            return modelAndView;
        }
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage(HttpSession httpSession) {
        if (isLoggedIn(httpSession)) {
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
    public ModelAndView getHomePage(HttpSession httpSession) {
        UUID userId = (UUID) httpSession.getAttribute("user_id");
        UserDto user = userService.findById(userId);

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("user", user);
        modelAndView.addObject("activePage", "home");
        return modelAndView;
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession httpSession) {
        httpSession.invalidate();
        return new ModelAndView("redirect:/");
    }

    private boolean isLoggedIn(HttpSession httpSession) {
        return httpSession != null && httpSession.getAttribute("user_id") != null;
    }
}
