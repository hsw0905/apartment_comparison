package me.harry.apartment_comparison.presentation.controller;

import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.dto.response.common.ApiResponse;
import me.harry.apartment_comparison.application.dto.response.common.Type;
import me.harry.apartment_comparison.presentation.dto.request.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    @GetMapping
    public String checkSecurity() {
        return "Access Success";
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest dto) {
        return new ResponseEntity<>(ApiResponse.of(Type.SUCCESS, new LoginResponse(
                "accessToken", "refreshToken"
        )), HttpStatus.CREATED);
    }
}
