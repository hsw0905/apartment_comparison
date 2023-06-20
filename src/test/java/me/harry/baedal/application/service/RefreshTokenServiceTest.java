package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.LoginServiceRequest;
import me.harry.baedal.application.dto.request.RefreshServiceRequest;
import me.harry.baedal.application.dto.response.LoginResponse;
import me.harry.baedal.application.dto.response.RefreshResponse;
import me.harry.baedal.application.exception.BadRequestException;
import me.harry.baedal.domain.model.RefreshToken;
import me.harry.baedal.domain.model.RefreshTokenId;
import me.harry.baedal.domain.model.UserRole;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.presentation.security.TokenGenerator;
import me.harry.baedal.presentation.security.TokenType;
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
    private LoginService loginService;
    @Autowired
    private RedisDao redisDao;
    @Autowired
    private TokenGenerator tokenGenerator;
    private String blacklistToken;
    private static String PASSWORD = "Abcd123!";
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", PASSWORD, UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        if (loginResponse != null ) {
            cleanRedis();
        }
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("올바른 리프레시 토큰을 사용하면 새 토큰으로 갱신할 수 있다.")
    @Test
    void refreshTokenSuccess() {
        // given
        loginResponse = loginService.login(new LoginServiceRequest(testUser.getEmail(), PASSWORD));

        RefreshServiceRequest dto = new RefreshServiceRequest(testUser.getId().toString(), testUser.getRole().toString(),
                TokenType.REFRESH.toString(), loginResponse.refreshToken());

        // when
        RefreshResponse refreshResponse = refreshTokenService.refresh(dto);

        // then
        assertThat(refreshTokenRepository.findByUser(testUser).isPresent()).isTrue();
        assertThat(refreshResponse.refreshToken()).isNotEmpty();
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


    private void cleanRedis() {
        if (redisDao.hasKey(loginResponse.accessToken())) {
            redisDao.delete(List.of(loginResponse.accessToken()));
        }
        else if (redisDao.hasKey(loginResponse.refreshToken())) {
            redisDao.delete(List.of(loginResponse.refreshToken()));
        }
    }
}
