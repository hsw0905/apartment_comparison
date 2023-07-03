package me.harry.baedal.presentation.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.harry.baedal.presentation.dto.response.common.ApiResponse;
import me.harry.baedal.presentation.dto.response.common.ErrorResponse;
import me.harry.baedal.presentation.dto.response.common.Type;
import me.harry.baedal.application.exception.UnAuthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public ExceptionHandlerFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (UnAuthorizedException e) {
            sendErrorResponse(ApiResponse.of(
                    Type.FAILURE, HttpStatus.UNAUTHORIZED.value(),
                    new ErrorResponse(HttpStatus.UNAUTHORIZED.toString(), e.getMessage())), response);
        }
    }

    private void sendErrorResponse(ApiResponse<ErrorResponse> apiResponse, HttpServletResponse response) {
        response.setStatus(apiResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } catch (IOException e) {
            log.error("[ExceptionHandlerFilter]- {}", e.getMessage(), e);
        }
    }

}
