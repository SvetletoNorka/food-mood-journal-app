package app.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Username or password mismatch!");
    }
}
