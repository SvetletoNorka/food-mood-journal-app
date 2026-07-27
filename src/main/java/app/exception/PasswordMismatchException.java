package app.exception;

public class PasswordMismatchException extends DomainException {

    public PasswordMismatchException() {
        super("New password and confirmation do not match.");
    }
}
