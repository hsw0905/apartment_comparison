package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.request.LogoutServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.domain.model.BlackList;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.domain.repository.BlackListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class LogoutServiceTest extends ServiceTest {
    @Autowired
    private LoginService loginService;

    @Autowired
    private LogoutService logoutService;

    @Autowired
    private BlackListRepository blackListRepository;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", "1234", UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        blackListRepository.deleteAll();
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그아웃시 해당 사용자의 refreshToken이 삭제되며, redis에 blacklist 토큰이 등록된다.")
    @Test
    void logoutSuccess() {
        // given
        LoginResponse loginResponse = loginService.login(new LoginServiceRequest(testUser.getEmail(), "1234"));

        // when
        logoutService.logout(new LogoutServiceRequest(testUser.getId().toString(), loginResponse.accessToken()));

        // then
        Iterable<BlackList> blackLists = blackListRepository.findAll();

        assertThat(blackLists).hasSize(2);
        assertThat(refreshTokenRepository.findById(loginResponse.refreshToken()).isEmpty()).isTrue();
    }
}
