package app.exception;

public class DuplicateUsernameException extends DomainException {

    public DuplicateUsernameException() {
        super("User with this username already exists!");
    }
}
