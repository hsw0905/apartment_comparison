package me.harry.apartment_comparison.security.domain;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TokenGenerator {
    private final Algorithm algorithm;

    public TokenGenerator(@Value("${jwt.secret}") String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
    }

    public String generate(String userId, Instant expireTime) {
        return JWT.create()
                .withClaim("userId", userId)
                .withExpiresAt(expireTime)
                .sign(algorithm);
    }

    public boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
}
