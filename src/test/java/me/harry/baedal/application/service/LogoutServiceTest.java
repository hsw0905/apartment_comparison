package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.LoginServiceRequest;
import me.harry.baedal.application.dto.request.LogoutServiceRequest;
import me.harry.baedal.application.dto.response.LoginResponse;
import me.harry.baedal.domain.model.UserRole;
import me.harry.baedal.infrastructure.redis.RedisDao;
import me.harry.baedal.presentation.security.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutServiceTest extends ServiceTest {
    private static final String PASSWORD = "Abcd123!";
    @Autowired
    private LoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private RedisDao redisDao;

    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", PASSWORD, UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        redisDao.delete(List.of(loginResponse.accessToken(), loginResponse.refreshToken()));
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그아웃시 해당 사용자의 refreshToken이 삭제되며, redis에 blacklist 토큰이 등록된다.")
    @Test
    void logoutSuccess() {
        // given
        loginResponse = loginService.login(new LoginServiceRequest(testUser.getEmail(), PASSWORD));

        // when
        logoutService.logout(new LogoutServiceRequest(testUser.getId().toString(), TokenType.ACCESS.toString(), loginResponse.accessToken()));

        // then
        assertThat(redisDao.findByKey(loginResponse.accessToken())).isEqualTo(testUser.getId().toString());
        assertThat(redisDao.findByKey(loginResponse.refreshToken())).isEqualTo(testUser.getId().toString());
        assertThat(refreshTokenRepository.findByUser(testUser).isEmpty()).isTrue();
    }
}
