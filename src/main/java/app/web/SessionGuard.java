package app.web;

import app.config.UserSession;
import org.springframework.web.servlet.ModelAndView;

public final class SessionGuard {

    private SessionGuard() {
    }

    public static ModelAndView redirectToLoginIfNeeded(UserSession userSession) {
        if (!userSession.isLoggedIn()) {
            return new ModelAndView("redirect:/login");
        }
        return null;
    }
}
