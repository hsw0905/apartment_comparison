package me.harry.baedal.application.dto.request;

public record LogoutServiceRequest (String userId, String tokenType, String accessToken) {
}
