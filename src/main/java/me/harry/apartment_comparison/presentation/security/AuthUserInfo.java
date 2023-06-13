package me.harry.apartment_comparison.presentation.security;

public record AuthUserInfo(
        String id,
        String role,
        String accessToken
) {
    public static AuthUserInfo of(String id, String role, String accessToken) {
        return new AuthUserInfo(id, role, accessToken);
    }

}
