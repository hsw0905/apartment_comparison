package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.exception.LoginFailException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LoginServiceTest extends ServiceTest {
    @Autowired
    private LoginService loginService;


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

    @DisplayName("올바른 email과 password로 로그인시 성공한다.")
    @Test
    void loginSuccessWithEmailAndPassword() {
        // given
        LoginServiceRequest dto = new LoginServiceRequest(testUser.getEmail(), "1234");

        // when
        LoginResponse response = loginService.login(dto);

        // then
        Optional<RefreshToken> token = refreshTokenRepository.findById(testUser.getId().toString());

        assertThat(response.accessToken()).isNotBlank();
        assertThat(token).isNotNull();
    }

    @DisplayName("올바르지 않은 이메일로 로그인시 실패한다.")
    @Test
    void loginFailWithIncorrectEmail() {
        // given
        String wrongEmail = "xxx";
        LoginServiceRequest dto = new LoginServiceRequest(wrongEmail, "1234");

        // when then
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(LoginFailException.class);
    }

    @DisplayName("올바르지 않은 비밀번호로 로그인시 실패한다.")
    @Test
    void loginFailWithIncorrectPassword() {
        // given
        String wrongPassword = "xxx";
        LoginServiceRequest dto = new LoginServiceRequest(testUser.getEmail(), wrongPassword);

        // when then
        assertThatThrownBy(() -> loginService.login(dto))
                .isInstanceOf(LoginFailException.class);
    }

}
