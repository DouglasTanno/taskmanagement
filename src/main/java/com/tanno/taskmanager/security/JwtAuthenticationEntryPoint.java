package com.tanno.taskmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);

        problem.setTitle("Unauthorized");
        problem.setDetail("Authentication is required.");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        new ObjectMapper()
                .writeValue(response.getWriter(), problem);
    }
}