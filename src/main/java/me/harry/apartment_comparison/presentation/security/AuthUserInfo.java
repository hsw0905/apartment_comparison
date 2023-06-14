package me.harry.apartment_comparison.presentation.security;

public record AuthUserInfo(
        String id,
        String role,
        String tokenType,
        String token
) {
    public static AuthUserInfo of(String id, String role, String tokenType, String token) {
        return new AuthUserInfo(id, role, tokenType, token);
    }

}
