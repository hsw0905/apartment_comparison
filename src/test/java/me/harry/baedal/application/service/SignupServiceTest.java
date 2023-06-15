package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.SignupServiceRequest;
import me.harry.baedal.application.exception.ForbiddenException;
import me.harry.baedal.domain.exception.DuplicatedUserException;
import me.harry.baedal.domain.model.User;
import me.harry.baedal.domain.model.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SignupServiceTest extends ServiceTest {
    private final String email = "test@example.com";
    private final String password = "Abcd123!";
    private final String name = "tester";
    @Autowired
    private SignupService signupService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("올바른 정보를 입력하면 회원가입에 성공한다.")
    @Test
    void signupSuccess() {
        // given
        SignupServiceRequest dto = new SignupServiceRequest(email, password, name);

        // when
        signupService.signup(dto);

        // then
        Optional<User> user = userRepository.findByEmail(email);
        assertThat(user.isPresent()).isTrue();
    }

    @DisplayName("이미 가입된 이메일인 경우 사용자는 가입할 수 없다.")
    @Test
    void signupFailWithDuplicatedUser() {
        // given
        testUser = createUser(name, email, password, UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);

        SignupServiceRequest dto = new SignupServiceRequest(email, password, name);

        // when then
        assertThatThrownBy(() -> signupService.signup(dto))
                .isInstanceOf(DuplicatedUserException.class);
    }

    @DisplayName("탈퇴한 사용자가 다시 가입할 경우 재가입된다.")
    @Test
    void signupSuccessWithExpiredUser() {
        // given
        testUser = createUser(name, email, password, UserRole.ROLE_USER, true, true);
        userRepository.save(testUser);

        SignupServiceRequest dto = new SignupServiceRequest(email, password, name);
        // when
        signupService.signup(dto);

        // then
        Optional<User> user = userRepository.findByEmail(email);
        assertThat(user.isPresent()).isTrue();
    }

    @DisplayName("비활성화된 사용자는 가입할 수 없으며, 403을 반환한다.")
    @Test
    void signupFailWithDeactivateUser() {
        // given
        testUser = createUser(name, email, password, UserRole.ROLE_USER, false, false);
        userRepository.save(testUser);

        SignupServiceRequest dto = new SignupServiceRequest(email, password, name);

        // when then
        assertThatThrownBy(() -> signupService.signup(dto))
                .isInstanceOf(ForbiddenException.class);
    }
}
