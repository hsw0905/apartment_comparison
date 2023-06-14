package me.harry.apartment_comparison.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.application.exception.BadRequestException;
import me.harry.apartment_comparison.application.exception.NotFoundException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.User;
import me.harry.apartment_comparison.domain.model.UserId;
import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.domain.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LogoutService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, RedisTemplate<String, Object> redisTemplate,
                         @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public void logout(LogoutServiceRequest dto) {
        try {
            validateAccessType(dto.tokenType());
            User user = getUser(dto.userId());
            RefreshToken refreshToken = getRefreshToken(user);

            if (isRedisReady()) {
                redisTemplate.opsForValue().set(dto.accessToken(), dto.userId(), accessTokenExpireTime, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(refreshToken.getRefreshToken(), dto.userId(), refreshTokenExpireTime, TimeUnit.SECONDS);
            }
            refreshTokenRepository.deleteByUser(user);
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection");
        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId());
        }
    }

    private RefreshToken getRefreshToken(User user) {
        return refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("토큰을 찾을 수 없습니다. 재로그인이 필요합니다."));
    }

    private boolean isRedisReady() {
        return redisTemplate.getRequiredConnectionFactory().getConnection().ping() != null;
    }

    private void validateAccessType(String tokenType) {
        if (!tokenType.equals(TokenType.ACCESS.toString())) {
            throw new BadRequestException("유효한 accessToken이 아닙니다.");
        }
    }

    private User getUser(String userId) {
        return userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
    }
}
