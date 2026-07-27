package app.web;

import app.exception.DomainException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleDomainException(DomainException ex) {
        return errorView(ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ModelAndView handleAccessDenied(AccessDeniedException ex) {
        return errorView("You do not have permission to access this resource.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidation(Exception ex) {
        FieldError fieldError = null;
        if (ex instanceof MethodArgumentNotValidException manv) {
            fieldError = manv.getBindingResult().getFieldError();
        } else if (ex instanceof BindException bindException) {
            fieldError = bindException.getBindingResult().getFieldError();
        }

        String message = fieldError != null
                ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                : "Invalid input. Please check the submitted data.";
        return errorView(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return errorView("Invalid value for parameter '" + ex.getName() + "'.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoResource(NoResourceFoundException ex) {
        return errorView("The requested page was not found.");
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ModelAndView handleFeignException(FeignException ex) {
        return errorView(
                "Insights service is unavailable or returned an error. Make sure it is running on port 8081. (status "
                        + ex.status() + ")");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneric(Exception ex) {
        return errorView("An unexpected error occurred. Please try again later.");
    }

    private ModelAndView errorView(String message) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", message);
        return modelAndView;
    }
}
