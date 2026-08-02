package app.util.security;

import app.security.AuthenticationMetadata;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TestSecurityUtils {

    private TestSecurityUtils() {
    }

    public static RequestPostProcessor withAuth(AuthenticationMetadata metadata) {
        return request -> {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            metadata,
                            metadata.getPassword(),
                            metadata.getAuthorities());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            return request;
        };
    }

    public static void clearAuth() {
        SecurityContextHolder.clearContext();
    }
}
