package me.harry.apartment_comparison.presentation.controller;

import jakarta.validation.Valid;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.service.LoginService;
import me.harry.apartment_comparison.presentation.dto.request.LoginRequest;
import me.harry.apartment_comparison.presentation.dto.response.common.ApiResponse;
import me.harry.apartment_comparison.presentation.dto.response.common.Type;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final LoginService loginService;

    public AuthenticationController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public String checkSecurity() {
        return "Access Success";
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest dto) {
        LoginResponse response = loginService.login(dto.toServiceDto());
        return new ResponseEntity<>(
                ApiResponse.of(Type.SUCCESS, HttpStatus.CREATED.value(), response),
                HttpStatus.CREATED
        );
    }
}
