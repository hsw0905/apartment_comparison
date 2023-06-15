package me.harry.baedal.presentation.controller;


import jakarta.validation.Valid;
import me.harry.baedal.application.service.SignupService;
import me.harry.baedal.presentation.dto.request.SignupRequest;
import me.harry.baedal.presentation.dto.response.common.ApiResponse;
import me.harry.baedal.presentation.dto.response.common.Type;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final SignupService signupService;

    public UserController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest dto) {
        signupService.signup(dto.toServiceDto());

        return new ResponseEntity<>(
                ApiResponse.of(Type.SUCCESS, HttpStatus.CREATED.value(), null),
                HttpStatus.CREATED
        );
    }
}
