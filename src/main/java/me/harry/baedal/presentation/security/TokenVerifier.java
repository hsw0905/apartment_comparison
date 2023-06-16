package me.harry.baedal.presentation.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.harry.baedal.application.exception.UnAuthorizedException;
import me.harry.baedal.infrastructure.redis.RedisDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenVerifier {
    private final Algorithm algorithm;
    private final RedisDao redisDao;

    public TokenVerifier(@Value("${jwt.secret}") String secret, RedisDao redisDao) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.redisDao = redisDao;
    }

    public boolean verify(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            validateBlackList(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new UnAuthorizedException("유효기간이 만료된 토큰입니다.");
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public AuthUserInfo extractAuthUserInfo(String token) {
        try {
            DecodedJWT decoded = JWT.decode(token);
            Claim userId = decoded.getClaim("userId");
            Claim role = decoded.getClaim("role");
            Claim tokenType = decoded.getClaim("type");

            return AuthUserInfo.of(userId.asString(), role.asString(), tokenType.asString(), token);
        } catch (JWTDecodeException e) {
            throw new UnAuthorizedException("올바르지 않은 JWT Format 입니다.");
        }
    }

    private void validateBlackList(String token) {
        if (redisDao.isRedisReady()) {
            if (redisDao.findByKey(token) != null) {
                throw new UnAuthorizedException("유효하지 않은 토큰입니다. 재로그인이 필요합니다.");
            }
        }
    }
}
