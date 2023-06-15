package me.harry.baedal.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import me.harry.baedal.application.dto.request.LoginServiceRequest;

public record LoginRequest(
        @Email
        String email,
        @NotBlank
        String password
) {
    public LoginServiceRequest toServiceDto() {
        return new LoginServiceRequest(email, password);
    }
}
