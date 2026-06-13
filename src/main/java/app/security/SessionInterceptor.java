package app.security;

import app.model.dto.user.UserDto;
import app.model.entity.user.UserRole;
import app.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final Set<String> UNAUTHENTICATED_ENDPOINTS = Set.of("/", "/login", "/register", "/error");

    private final UserService userService;

    public SessionInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String endpoint = request.getServletPath();

        if (UNAUTHENTICATED_ENDPOINTS.contains(endpoint)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
            return false;
        }

        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }

        UserDto user = userService.findById(userId);

        if (!user.isActive()) {
            session.invalidate();
            response.sendRedirect("/login");
            return false;
        }

        if (isAdminUsersEndpoint(endpoint) && user.getRole() != UserRole.ADMIN) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("You do not have permission to access this resource.");
            return false;
        }

        return true;
    }

    private boolean isAdminUsersEndpoint(String endpoint) {
        return endpoint.equals("/users")
                || endpoint.matches("/users/.+/status")
                || endpoint.matches("/users/.+/role");
    }
}
