package me.harry.apartment_comparison.presentation.controller;

import jakarta.validation.Valid;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.service.LoginService;
import me.harry.apartment_comparison.application.service.LogoutService;
import me.harry.apartment_comparison.presentation.dto.request.LoginRequest;
import me.harry.apartment_comparison.presentation.dto.response.common.ApiResponse;
import me.harry.apartment_comparison.presentation.dto.response.common.Type;
import me.harry.apartment_comparison.presentation.security.AuthUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final LoginService loginService;
    private final LogoutService logoutService;

    public AuthenticationController(LoginService loginService, LogoutService logoutService) {
        this.loginService = loginService;
        this.logoutService = logoutService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest dto) {
        LoginResponse response = loginService.login(dto.toServiceDto());
        return new ResponseEntity<>(
                ApiResponse.of(Type.SUCCESS, HttpStatus.CREATED.value(), response),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        AuthUserInfo authUserInfo = (AuthUserInfo) authentication.getPrincipal();
        logoutService.logout(new LogoutServiceRequest(authUserInfo.id(), authUserInfo.accessToken()));

        return ResponseEntity.ok(ApiResponse.of(Type.SUCCESS, HttpStatus.OK.value(), null));
    }
}
