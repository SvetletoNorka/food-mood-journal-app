package app.web;

import app.exception.DomainException;
import feign.FeignException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ModelAndView handleDomainException(DomainException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        return modelAndView;
    }

    @ExceptionHandler(FeignException.class)
    public ModelAndView handleFeignException(FeignException ex) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject(
                "errorMessage",
                "Insights service is unavailable or returned an error. Make sure it is running on port 8081. ("
                        + ex.status() + ")");
        return modelAndView;
    }
}
