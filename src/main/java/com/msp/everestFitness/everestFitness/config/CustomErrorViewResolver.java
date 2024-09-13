package com.msp.everestFitness.everestFitness.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Component
public class CustomErrorViewResolver implements ErrorViewResolver {

    @Override
    public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
        String viewName = "error";
        if (status == HttpStatus.FORBIDDEN) {
            viewName = "403"; // Thymeleaf template for 403 error
        } else if (status == HttpStatus.NOT_FOUND) {
            viewName = "404"; // Thymeleaf template for 404 error
        }else if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            viewName = "500"; // Thymeleaf template for 500 error
        }
        return new ModelAndView(viewName,model);
    }
}
