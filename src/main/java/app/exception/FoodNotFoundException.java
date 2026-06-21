package app.exception;

public class FoodNotFoundException extends DomainException {

    public FoodNotFoundException() {
        super("Food not found.");
    }
}
