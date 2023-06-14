package me.harry.apartment_comparison.application.dto.request;

public record RefreshServiceRequest(String userId, String role, String tokenType, String refreshToken) {
}
