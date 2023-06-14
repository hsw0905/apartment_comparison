package me.harry.apartment_comparison.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.application.exception.BadRequestException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LogoutService(RefreshTokenRepository refreshTokenRepository, RedisTemplate<String, Object> redisTemplate,
                         @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisTemplate = redisTemplate;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public void logout(LogoutServiceRequest dto) {
        try {
            validateAccessType(dto.tokenType());
            RefreshToken refreshToken = refreshTokenRepository.findByUserId(dto.userId()).get();

            if (isRedisReady()) {
                redisTemplate.opsForValue().set(dto.accessToken(), dto.userId(), accessTokenExpireTime, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(refreshToken.getRefreshToken(), dto.userId(), refreshTokenExpireTime, TimeUnit.SECONDS);
            }
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection");
        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId());
        }

        refreshTokenRepository.deleteByUserId(dto.userId());
    }

    private boolean isRedisReady() {
        return redisTemplate.getRequiredConnectionFactory().getConnection().ping() != null;
    }

    private void validateAccessType(String tokenType) {
        if (!tokenType.equals(TokenType.ACCESS.toString())) {
            throw new BadRequestException("유효한 accessToken이 아닙니다.");
        }
    }
}
