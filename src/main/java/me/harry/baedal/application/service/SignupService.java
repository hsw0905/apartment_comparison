package me.harry.baedal.application.service;

import me.harry.baedal.application.dto.request.SignupServiceRequest;
import me.harry.baedal.application.exception.ForbiddenException;
import me.harry.baedal.domain.exception.DuplicatedUserException;
import me.harry.baedal.domain.model.User;
import me.harry.baedal.domain.model.UserId;
import me.harry.baedal.domain.model.UserRole;
import me.harry.baedal.infrastructure.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SignupService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void signup(SignupServiceRequest dto) {
        Optional<User> duplicatedUser = userRepository.findByEmail(dto.email());

        if (duplicatedUser.isEmpty()) {
            userRepository.save(
                    User.builder()
                            .id(UserId.generate())
                            .email(dto.email())
                            .name(dto.name())
                            .encodedPassword(passwordEncoder.encode(dto.password()))
                            .role(UserRole.ROLE_USER)
                            .isOut(false)
                            .isAvailable(true)
                            .build()
            );
            return;
        }

        User user = duplicatedUser.get();

        if (!user.isAvailable()) {
            throw new ForbiddenException("사용할 수 없는 계정입니다. 관리자에게 문의하세요.");
        }

        if (user.isOut()) {
            user.reSignup();
        } else {
            throw new DuplicatedUserException("이미 가입된 사용자입니다.");
        }

    }


}
