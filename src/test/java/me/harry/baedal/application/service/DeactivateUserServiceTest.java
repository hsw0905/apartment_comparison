package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.DeactivateUserServiceRequest;
import me.harry.baedal.domain.exception.UserNotFoundException;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeactivateUserServiceTest extends ServiceTest {
    @Autowired
    private DeactivateUserService deactivateUserService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        testUser = createUser("tester", "test@example.com", "1234", UserRole.ROLE_USER, false, true);
        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자는 탈퇴할 수 있다.")
    @Test
    void deactivateSuccess() {
        // given
        DeactivateUserServiceRequest dto = new DeactivateUserServiceRequest(testUser.getId().toString());

        // when
        deactivateUserService.deactivate(dto);

        // then
        User user = userRepository.findById(testUser.getId()).get();

        assertThat(user.isOut()).isTrue();
    }

    @DisplayName("회원이 아닌 사용자는 탈퇴 할 수 없으며, 404를 반환한다.")
    @Test
    void deactivateFailWithNoUser() {
        // given
        DeactivateUserServiceRequest dto = new DeactivateUserServiceRequest(UserId.generate().toString());
        // when then
        assertThatThrownBy(()->deactivateUserService.deactivate(dto))
                .isInstanceOf(UserNotFoundException.class);
    }
}
