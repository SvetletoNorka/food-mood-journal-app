package app.exception;

public class DuplicateEmailException extends DomainException {

    public DuplicateEmailException() {
        super("User with this email already exists!");
    }
}
