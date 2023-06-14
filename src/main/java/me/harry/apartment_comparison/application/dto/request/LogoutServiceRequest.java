package me.harry.apartment_comparison.application.dto.request;

public record LogoutServiceRequest (String userId, String tokenType, String accessToken) {
}
