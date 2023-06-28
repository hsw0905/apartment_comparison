package me.harry.baedal.application.service;

import me.harry.baedal.BaedalApplication;
import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import me.harry.baedal.domain.model.user.UserRole;
import me.harry.baedal.infrastructure.config.RedisConfig;
import me.harry.baedal.infrastructure.config.WebSecurityConfig;
import me.harry.baedal.infrastructure.repository.RefreshTokenRepository;
import me.harry.baedal.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {
        BaedalApplication.class,
        WebSecurityConfig.class,
        RedisConfig.class
})
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
