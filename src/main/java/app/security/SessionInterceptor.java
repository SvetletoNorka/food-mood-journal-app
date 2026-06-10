package app.security;

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

        userService.findById(userId);

        return true;
    }
}
