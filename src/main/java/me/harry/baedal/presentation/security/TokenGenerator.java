package me.harry.baedal.presentation.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import me.harry.baedal.domain.model.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenGenerator {
    private final Algorithm algorithm;

    public TokenGenerator(@Value("${jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generate(String userId, UserRole role, TokenType tokenType, Instant expireTime) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", role.toString())
                .withClaim("type", tokenType.toString())
                .withExpiresAt(expireTime)
                .sign(algorithm);
    }
}
