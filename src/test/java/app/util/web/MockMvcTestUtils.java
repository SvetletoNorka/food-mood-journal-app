package app.util.web;

import app.web.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public final class MockMvcTestUtils {

    private MockMvcTestUtils() {
    }

    public static MockMvc standalone(Object... controllers) {
        return standaloneWithAdvice(new GlobalExceptionHandler(), controllers);
    }

    public static MockMvc standaloneWithAdvice(Object advice, Object... controllers) {
        return MockMvcBuilders.standaloneSetup(controllers)
                .setControllerAdvice(advice)
                .setViewResolvers(viewResolver())
                .build();
    }

    public static MockMvc standaloneWithAdvice(Object[] advice, Object... controllers) {
        return MockMvcBuilders.standaloneSetup(controllers)
                .setControllerAdvice(advice)
                .setViewResolvers(viewResolver())
                .build();
    }

    private static ViewResolver viewResolver() {
        return (viewName, locale) -> {
            if (viewName.startsWith("redirect:")) {
                return new RedirectView(viewName.substring("redirect:".length()), true, false);
            }
            return new AbstractView() {
                @Override
                protected void renderMergedOutputModel(Map<String, Object> model,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
                }
            };
        };
    }
}
