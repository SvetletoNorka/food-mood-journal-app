package app.web.user;

import app.exception.DomainException;
import app.model.dto.user.EditProfileRequest;
import app.model.dto.user.UserDto;
import app.security.AuthenticationUtils;
import app.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping
    public ModelAndView viewProfile() {
        UUID userId = AuthenticationUtils.getCurrentUserId();
        UserDto user = userService.findById(userId);

        ModelAndView modelAndView = new ModelAndView("profile");
        modelAndView.addObject("user", user);
        modelAndView.addObject("activePage", "profile");
        return modelAndView;
    }

    @GetMapping("/edit")
    public ModelAndView editProfileForm() {
        UUID userId = AuthenticationUtils.getCurrentUserId();
        UserDto user = userService.findById(userId);

        EditProfileRequest editProfileRequest = EditProfileRequest.builder()
                .email(user.getEmail())
                .build();

        ModelAndView modelAndView = new ModelAndView("profile-edit");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editProfileRequest", editProfileRequest);
        modelAndView.addObject("activePage", "profile");
        return modelAndView;
    }

    @PostMapping
    public ModelAndView updateProfile(@Valid @ModelAttribute("editProfileRequest") EditProfileRequest editProfileRequest,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
        UUID userId = AuthenticationUtils.getCurrentUserId();
        UserDto user = userService.findById(userId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("profile-edit");
            modelAndView.addObject("user", user);
            modelAndView.addObject("activePage", "profile");
            return modelAndView;
        }

        try {
            userService.updateProfile(userId, editProfileRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
            return new ModelAndView("redirect:/profile");
        } catch (DomainException ex) {
            ModelAndView modelAndView = new ModelAndView("profile-edit");
            modelAndView.addObject("user", user);
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("activePage", "profile");
            return modelAndView;
        }
    }
}
