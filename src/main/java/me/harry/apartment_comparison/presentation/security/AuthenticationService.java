package me.harry.apartment_comparison.presentation.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthenticationService {
    private final TokenVerifier tokenVerifier;

    public AuthenticationService(TokenVerifier tokenVerifier) {
        this.tokenVerifier = tokenVerifier;
    }

    public Authentication authenticate(String token) {
        if (!tokenVerifier.verify(token)) {
            return null;
        }
        AuthUserInfo authUserInfo = AuthUserInfo.authenticated("id", "ROLE_USER", token);

        return UsernamePasswordAuthenticationToken.authenticated(authUserInfo, null, List.of(authUserInfo::role));

    }
}
