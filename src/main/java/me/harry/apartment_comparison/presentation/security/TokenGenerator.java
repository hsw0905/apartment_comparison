package me.harry.apartment_comparison.presentation.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import me.harry.apartment_comparison.domain.model.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenGenerator {
    private final Algorithm algorithm;

    public TokenGenerator(@Value("${jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generate(String userId, UserRole role, Instant expireTime) {
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("role", role.toString())
                .withExpiresAt(expireTime)
                .sign(algorithm);
    }
}
