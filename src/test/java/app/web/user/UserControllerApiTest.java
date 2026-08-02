package app.web.user;

import app.model.dto.user.UserDto;
import app.service.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static app.util.security.TestSecurityUtils.clearAuth;
import static app.util.security.TestSecurityUtils.withAuth;
import static app.util.user.UserFactory.getAdminUser;
import static app.util.user.UserFactory.getUserDto;
import static app.util.web.MockMvcTestUtils.standalone;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(MockitoExtension.class)
class UserControllerApiTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standalone(userController);
    }

    @AfterEach
    void tearDown() {
        clearAuth();
    }

    @Test
    void getAllUsers_whenUserIsAdmin_thenReturnStatus200AndUsersView() throws Exception {
        var admin = getAdminUser();
        List<UserDto> users = List.of(getUserDto(), getUserDto(), getUserDto());
        when(userService.getAllUsers()).thenReturn(users);

        MockHttpServletRequestBuilder request = get("/users").with(withAuth(admin));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("users"))
                .andExpect(MockMvcResultMatchers.model().attribute("users", users))
                .andExpect(MockMvcResultMatchers.model().attribute("activePage", "users"));
    }

    @Test
    void putSwitchStatus_shouldRedirectToUsers() throws Exception {
        var admin = getAdminUser();
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/users/{id}/status", id).with(withAuth(admin)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/users"));

        verify(userService).switchStatus(id);
    }

    @Test
    void putSwitchRole_shouldRedirectToUsers() throws Exception {
        var admin = getAdminUser();
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/users/{id}/role", id).with(withAuth(admin)))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/users"));

        verify(userService).switchRole(id);
    }
}
