package me.harry.baedal.presentation.controller;


import jakarta.validation.Valid;
import me.harry.baedal.application.dto.request.DeactivateUserServiceRequest;
import me.harry.baedal.application.service.DeactivateUserService;
import me.harry.baedal.application.service.SignupService;
import me.harry.baedal.presentation.dto.request.SignupRequest;
import me.harry.baedal.presentation.dto.response.common.ApiResponse;
import me.harry.baedal.presentation.dto.response.common.Type;
import me.harry.baedal.presentation.security.AuthUserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final SignupService signupService;
    private final DeactivateUserService deactivateUserService;

    public UserController(SignupService signupService, DeactivateUserService deactivateUserService) {
        this.signupService = signupService;
        this.deactivateUserService = deactivateUserService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest dto) {
        signupService.signup(dto.toServiceDto());

        return new ResponseEntity<>(
                ApiResponse.of(Type.SUCCESS, HttpStatus.CREATED.value(), null),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deactivateUser(Authentication authentication) {
        AuthUserInfo authUserInfo = (AuthUserInfo) authentication.getPrincipal();

        deactivateUserService.deactivate(new DeactivateUserServiceRequest(authUserInfo.id()));

        return ResponseEntity.ok(ApiResponse.of(Type.SUCCESS, HttpStatus.OK.value(), null));
    }
}
