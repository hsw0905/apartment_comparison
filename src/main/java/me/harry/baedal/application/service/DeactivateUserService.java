package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.DeactivateUserServiceRequest;
import me.harry.baedal.domain.exception.UserNotFoundException;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateUserService {
    private final UserRepository userRepository;

    public DeactivateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void deactivate(DeactivateUserServiceRequest dto) {
        User user = getUser(dto.userId());
        user.deactivateUser();
    }

    private User getUser(String userId) {
        return userRepository.findById(new UserId(userId))
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }
}
