package app.exception;

import java.util.UUID;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(UUID id) {
        super("User with id [%s] does not exist.".formatted(id));
    }
}
