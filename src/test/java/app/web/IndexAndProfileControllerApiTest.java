package app.web;

import app.exception.DuplicateUsernameException;
import app.model.dto.user.UserDto;
import app.security.AuthenticationMetadata;
import app.service.user.UserService;
import app.web.user.ProfileController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static app.util.security.TestSecurityUtils.clearAuth;
import static app.util.security.TestSecurityUtils.withAuth;
import static app.util.user.UserFactory.getUserDto;
import static app.util.user.UserFactory.getUserPrincipal;
import static app.util.web.MockMvcTestUtils.standaloneWithAdvice;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class IndexAndProfileControllerApiTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneWithAdvice(
                new Object[]{new GlobalExceptionHandler(), new UserModelAdvice()},
                new IndexController(userService),
                new ProfileController(userService));
    }

    @AfterEach
    void tearDown() {
        clearAuth();
    }

    @Test
    void index_shouldReturnIndexView_whenAnonymous() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }

    @Test
    void index_shouldRedirectHome_whenAuthenticated() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        mockMvc.perform(get("/").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/home"));
    }

    @Test
    void login_shouldShowErrorMessage_whenErrorParamPresent() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("login"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    void login_shouldShowDisabledMessage_whenDisabledParamPresent() throws Exception {
        mockMvc.perform(get("/login").param("disabled", ""))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    void registerGet_shouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userRegisterRequest"));
    }

    @Test
    void registerPost_shouldRedirectToLogin_whenValid() throws Exception {
        when(userService.register(any())).thenReturn(getUserDto());

        mockMvc.perform(post("/register")
                        .param("username", "User123")
                        .param("email", "user123@example.com")
                        .param("password", "Password"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
    }

    @Test
    void registerPost_shouldReturnRegisterView_whenDomainException() throws Exception {
        when(userService.register(any())).thenThrow(new DuplicateUsernameException());

        mockMvc.perform(post("/register")
                        .param("username", "User123")
                        .param("email", "user123@example.com")
                        .param("password", "Password"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("errorMessage"));
    }

    @Test
    void home_shouldReturnHomeView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UserDto userDto = getUserDto();
        when(userService.findById(principal.getUserId())).thenReturn(userDto);

        mockMvc.perform(get("/home").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("home"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto));
    }

    @Test
    void accessDenied_shouldReturnErrorView() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("error"));
    }

    @Test
    void profile_shouldReturnProfileView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        UserDto userDto = getUserDto();
        when(userService.findById(principal.getUserId())).thenReturn(userDto);

        mockMvc.perform(get("/profile").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", userDto));
    }

    @Test
    void editProfileForm_shouldReturnProfileEditView() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        when(userService.findById(principal.getUserId())).thenReturn(getUserDto());

        mockMvc.perform(get("/profile/edit").with(withAuth(principal)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("profile-edit"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("editProfileRequest"));
    }

    @Test
    void updateProfile_shouldRedirect_whenValid() throws Exception {
        AuthenticationMetadata principal = getUserPrincipal();
        when(userService.findById(principal.getUserId())).thenReturn(getUserDto());
        when(userService.updateProfile(eq(principal.getUserId()), any())).thenReturn(getUserDto());

        mockMvc.perform(post("/profile")
                        .with(withAuth(principal))
                        .param("email", "new@example.com"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/profile"));

        verify(userService).updateProfile(eq(principal.getUserId()), any());
    }
}
