package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.application.dto.request.RefreshServiceRequest;
import me.harry.apartment_comparison.application.dto.response.RefreshResponse;
import me.harry.apartment_comparison.application.exception.BadRequestException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.infrastructure.redis.RedisDao;
import me.harry.apartment_comparison.infrastructure.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.presentation.security.TokenGenerator;
import me.harry.apartment_comparison.presentation.security.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class RefreshTokenServiceTest extends ServiceTest {
    private final long refreshTokenExpireTime = 1209600;
    private final long accessTokenExpireTime = 7200;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TokenGenerator tokenGenerator;
    private String blacklistToken;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", "1234", UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("올바른 리프레시 토큰을 사용하면 새 토큰으로 갱신할 수 있다.")
    @Test
    void refreshTokenSuccess() {
        // given
        String refreshToken = tokenGenerator.generate(testUser.getId().toString(), testUser.getRole(),
                TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        RefreshServiceRequest dto = new RefreshServiceRequest(testUser.getId().toString(), testUser.getRole().toString(),
                TokenType.REFRESH.toString(), refreshToken);

        refreshTokenRepository.save(new RefreshToken(refreshToken, testUser));

        // when
        RefreshResponse response = refreshTokenService.refresh(dto);

        // then
        assertThat(refreshTokenRepository.findById(refreshToken).isPresent()).isTrue();
        assertThat(response.refreshToken()).isEqualTo(refreshToken);
    }

    @DisplayName("블랙리스트 토큰인 경우 갱신에 실패한다.")
    @Test
    void refreshTokenFailWithBlackListToken() {
        // given
        blacklistToken = tokenGenerator.generate(testUser.getId().toString(), testUser.getRole(),
                TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        redisDao.setValueWithExpireTime(blacklistToken, testUser.getId().toString(), refreshTokenExpireTime, TimeUnit.SECONDS);

        RefreshServiceRequest dto = new RefreshServiceRequest(testUser.getId().toString(), testUser.getRole().toString(),
                TokenType.REFRESH.toString(), blacklistToken);

        // when then
        assertThatThrownBy(() -> refreshTokenService.refresh(dto))
                .isInstanceOf(BadRequestException.class);

        // tearDown
        redisDao.delete(List.of(blacklistToken));
    }

    @DisplayName("액세스 토큰인 경우 갱신에 실패한다.")
    @Test
    void refreshTokenFailWithAccessToken() {
        // given
        String accessToken = tokenGenerator.generate(testUser.getId().toString(), testUser.getRole(),
                TokenType.ACCESS, Instant.now().plusSeconds(accessTokenExpireTime));

        RefreshServiceRequest dto = new RefreshServiceRequest(testUser.getId().toString(), testUser.getRole().toString(),
                TokenType.ACCESS.toString(), accessToken);

        // when then
        assertThatThrownBy(() -> refreshTokenService.refresh(dto))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("로그아웃하여 DB에 refreshToken이 삭제된 경우 갱신 실패한다.")
    @Test
    void refreshTokenFailWhenTokenIsNotInDB() {
        // given
        String refreshToken = tokenGenerator.generate(testUser.getId().toString(), testUser.getRole(),
                TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));
        RefreshServiceRequest dto = new RefreshServiceRequest(testUser.getId().toString(), testUser.getRole().toString(),
                TokenType.REFRESH.toString(), refreshToken);

        // when then
        assertThatThrownBy(() -> refreshTokenService.refresh(dto))
                .isInstanceOf(BadRequestException.class);
    }
}
