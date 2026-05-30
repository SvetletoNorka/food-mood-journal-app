package app.config;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.UUID;

@SessionScope
@Component
public class UserSession {

    private UUID id;
    private String username;

    public void login(UUID id, String username) {
        this.id = id;
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return id != null;
    }

    public void logout() {
        this.id = null;
        this.username = null;
    }
}
