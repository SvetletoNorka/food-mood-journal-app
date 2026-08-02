package app.web;

import app.exception.FoodNotFoundException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerUnitTest {

    private final GlobalExceptionHandler underTest = new GlobalExceptionHandler();

    @Test
    void handleDomainException_shouldReturnErrorView() {
        ModelAndView mav = underTest.handleDomainException(new FoodNotFoundException());

        assertEquals("error", mav.getViewName());
        assertEquals("Food not found.", mav.getModel().get("errorMessage"));
    }

    @Test
    void handleAccessDenied_shouldReturnPermissionMessage() {
        ModelAndView mav = underTest.handleAccessDenied(new AccessDeniedException("denied"));

        assertEquals("error", mav.getViewName());
        assertTrue(mav.getModel().get("errorMessage").toString().contains("permission"));
    }

    @Test
    void handleValidation_shouldUseFieldErrorMessage() throws Exception {
        Method method = GlobalExceptionHandlerUnitTest.class.getDeclaredMethod("sampleMethod", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        bindingResult.addError(new FieldError("object", "name", "must not be blank"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ModelAndView mav = underTest.handleValidation(ex);

        assertEquals("name: must not be blank", mav.getModel().get("errorMessage"));
    }

    @Test
    void handleValidation_shouldHandleBindException() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "object");
        BindException ex = new BindException(bindingResult);

        ModelAndView mav = underTest.handleValidation(ex);

        assertEquals("Invalid input. Please check the submitted data.", mav.getModel().get("errorMessage"));
    }

    @Test
    void handleTypeMismatch_shouldIncludeParameterName() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", UUID.class, "id", null, null);

        ModelAndView mav = underTest.handleTypeMismatch(ex);

        assertTrue(mav.getModel().get("errorMessage").toString().contains("id"));
    }

    @Test
    void handleNoResource_shouldReturnNotFoundMessage() {
        ModelAndView mav = underTest.handleNoResource(new NoResourceFoundException(HttpMethod.GET, "/missing", "No static resource"));

        assertEquals("The requested page was not found.", mav.getModel().get("errorMessage"));
    }

    @Test
    void handleFeignException_shouldReturnBadGatewayMessage() {
        Request request = Request.create(Request.HttpMethod.GET, "/api", Collections.emptyMap(),
                null, StandardCharsets.UTF_8, new RequestTemplate());
        FeignException ex = new FeignException.ServiceUnavailable("down", request, null, Collections.emptyMap());

        ModelAndView mav = underTest.handleFeignException(ex);

        assertTrue(mav.getModel().get("errorMessage").toString().contains("Insights service"));
    }

    @Test
    void handleGeneric_shouldReturnUnexpectedMessage() {
        ModelAndView mav = underTest.handleGeneric(new RuntimeException("boom"));

        assertEquals("An unexpected error occurred. Please try again later.", mav.getModel().get("errorMessage"));
    }

    @SuppressWarnings("unused")
    private void sampleMethod(String name) {
    }
}
