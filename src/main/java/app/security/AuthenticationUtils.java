package app.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticationMetadata metadata)) {
            throw new IllegalStateException("No authenticated user in security context.");
        }
        return metadata.getUserId();
    }

    public static AuthenticationMetadata getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticationMetadata metadata)) {
            throw new IllegalStateException("No authenticated user in security context.");
        }
        return metadata;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof AuthenticationMetadata;
    }
}
