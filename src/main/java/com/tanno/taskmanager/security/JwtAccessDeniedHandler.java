package com.tanno.taskmanager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler
        implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException {

        ProblemDetail problem =
                ProblemDetail.forStatus(HttpStatus.FORBIDDEN);

        problem.setTitle("Forbidden");
        problem.setDetail("You don't have permission to access this resource.");

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        new ObjectMapper()
                .writeValue(response.getWriter(), problem);
    }
}