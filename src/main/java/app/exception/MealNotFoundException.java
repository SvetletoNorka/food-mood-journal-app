package app.exception;

public class MealNotFoundException extends DomainException {

    public MealNotFoundException() {
        super("Meal not found.");
    }
}
