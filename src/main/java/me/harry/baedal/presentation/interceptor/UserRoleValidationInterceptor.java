package me.harry.baedal.presentation.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.presentation.annotation.RoleOnly;
import me.harry.baedal.presentation.dto.response.common.ApiResponse;
import me.harry.baedal.presentation.dto.response.common.ErrorResponse;
import me.harry.baedal.presentation.dto.response.common.Type;
import me.harry.baedal.presentation.security.AuthUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;


@Slf4j
@Component
public class UserRoleValidationInterceptor implements HandlerInterceptor {
    private static final String UNAUTHORIZED_ROLE_MESSAGE = "허용되지 않는 사용자 역할입니다.";
    private final ObjectMapper objectMapper;

    public UserRoleValidationInterceptor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RoleOnly roleOnly = handlerMethod.getMethodAnnotation(RoleOnly.class);

        if (roleOnly == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUserInfo authUserInfo = (AuthUserInfo) authentication.getPrincipal();

        if (!isCurrentUserContainRole(authUserInfo.role(), roleOnly.roles())) {
            sendErrorResponse(ApiResponse.of(
                    Type.FAILURE, HttpStatus.UNAUTHORIZED.value(),
                    new ErrorResponse(HttpStatus.UNAUTHORIZED.toString(), UNAUTHORIZED_ROLE_MESSAGE)), response);
            return false;
        }

        return true;
    }

    private boolean isCurrentUserContainRole(String currentUserRole, UserRole[] userRoles) {
        return Arrays.stream(userRoles)
                .anyMatch(userRole -> userRole.toString().equals(currentUserRole));
    }


    private void sendErrorResponse(ApiResponse<ErrorResponse> apiResponse, HttpServletResponse response) {
        response.setStatus(apiResponse.getStatusCode());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        } catch (IOException e) {
            log.error("[UserRoleValidationInterceptor]- " + e.getMessage());
        }
    }
}
