package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.domain.model.User;
import me.harry.apartment_comparison.domain.model.UserId;
import me.harry.apartment_comparison.domain.model.UserRole;
import me.harry.apartment_comparison.infrastructure.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public abstract class ServiceTest {
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User testUser;

    protected User createUser(String name, String email, String password, UserRole role, boolean isOut, boolean isAvailable) {
        return User.builder()
                .id(UserId.generate())
                .name(name)
                .email(email)
                .encodedPassword(passwordEncoder.encode(password))
                .role(role)
                .isOut(isOut)
                .isAvailable(isAvailable).build();
    }
}
