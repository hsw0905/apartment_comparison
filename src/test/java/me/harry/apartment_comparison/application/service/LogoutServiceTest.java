package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.presentation.security.TokenType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutServiceTest extends ServiceTest {
    @Autowired
    private LoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", "1234", UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of(loginResponse.accessToken(), loginResponse.refreshToken()));
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그아웃시 해당 사용자의 refreshToken이 삭제되며, redis에 blacklist 토큰이 등록된다.")
    @Test
    void logoutSuccess() {
        // given
        loginResponse = loginService.login(new LoginServiceRequest(testUser.getEmail(), "1234"));

        // when
        logoutService.logout(new LogoutServiceRequest(testUser.getId().toString(), TokenType.ACCESS.toString(), loginResponse.accessToken()));

        // then
        assertThat(redisTemplate.opsForValue().get(loginResponse.accessToken())).isEqualTo(testUser.getId().toString());
        assertThat(redisTemplate.opsForValue().get(loginResponse.refreshToken())).isEqualTo(testUser.getId().toString());
        assertThat(refreshTokenRepository.findById(loginResponse.refreshToken()).isEmpty()).isTrue();
    }
}
