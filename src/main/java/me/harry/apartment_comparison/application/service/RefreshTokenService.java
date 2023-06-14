package me.harry.apartment_comparison.application.service;

import lombok.extern.slf4j.Slf4j;
import me.harry.apartment_comparison.application.dto.request.RefreshServiceRequest;
import me.harry.apartment_comparison.application.dto.response.RefreshResponse;
import me.harry.apartment_comparison.application.exception.BadRequestException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.presentation.security.TokenGenerator;
import me.harry.apartment_comparison.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class RefreshTokenService {
    private static final String INVALIDATE_REFRESH_TOKEN_MESSAGE = "유효한 refreshToken이 아닙니다.";
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenGenerator tokenGenerator;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, RedisTemplate<String, Object> redisTemplate, TokenGenerator tokenGenerator,
                               @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.redisTemplate = redisTemplate;
        this.tokenGenerator = tokenGenerator;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public RefreshResponse refresh(RefreshServiceRequest dto) {
        validateRefreshType(dto.tokenType());
        try {
            if (isRedisReady()) {
                validateBlackList(dto.refreshToken());
            }
        } catch (IllegalStateException e) {
            log.error("Redis is not Ready, Please Check Redis Connection");

        } catch (NoSuchElementException e) {
            log.error("Not found refreshToken, userId: " + dto.userId());
        }
        validateRefreshToken(dto);
        String accessToken = tokenGenerator.generate(dto.userId(), UserRole.ROLE_USER,
                TokenType.ACCESS, Instant.now().plusSeconds(accessTokenExpireTime));
        String refreshToken = tokenGenerator.generate(dto.userId(), UserRole.ROLE_USER,
                TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        refreshTokenRepository.save(new RefreshToken(refreshToken, dto.userId()));

        return new RefreshResponse(accessToken, refreshToken);
    }

    private void validateRefreshToken(RefreshServiceRequest dto) {
        if (refreshTokenRepository.findById(dto.refreshToken()).isEmpty()) {
            throw new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE);
        }
    }

    private boolean isRedisReady() {
        return redisTemplate.getRequiredConnectionFactory().getConnection().ping() != null;
    }

    private void validateRefreshType(String tokenType) {
        if (!tokenType.equals(TokenType.REFRESH.toString())) {
            throw new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE);
        }
    }

    private void validateBlackList(String refreshToken) {
        if (redisTemplate.opsForValue().get(refreshToken) != null) {
            throw new BadRequestException(INVALIDATE_REFRESH_TOKEN_MESSAGE);
        }
    }
}
