package me.harry.apartment_comparison.presentation.security;

public record AuthUserInfo(
        String id,
        String email,
        String password,
        String role,
        String accessToken
) {
    public static AuthUserInfo of(
            String id, String email, String password, String role) {
        return new AuthUserInfo(id, email, password, role, "");
    }

    public static AuthUserInfo authenticated(
            String id, String role, String accessToken) {
        return new AuthUserInfo(id, "", "", role, accessToken);
    }
}
