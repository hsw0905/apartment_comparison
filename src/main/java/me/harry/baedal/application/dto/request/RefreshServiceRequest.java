package me.harry.baedal.application.dto.request;

public record RefreshServiceRequest(String userId, String role, String tokenType, String refreshToken) {
}
