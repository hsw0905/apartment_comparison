package me.harry.apartment_comparison.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @Email
        String email,
        @NotBlank
        String password
) {
}
