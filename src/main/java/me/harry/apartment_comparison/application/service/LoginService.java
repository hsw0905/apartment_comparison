package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.application.dto.request.LoginServiceRequest;
import me.harry.apartment_comparison.application.dto.response.LoginResponse;
import me.harry.apartment_comparison.application.exception.LoginFailException;
import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.User;
import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import me.harry.apartment_comparison.domain.repository.UserRepository;
import me.harry.apartment_comparison.presentation.security.TokenGenerator;
import me.harry.apartment_comparison.presentation.security.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public LoginService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator,
                        @Value("${jwt.access-expired-time}") long accessTokenExpireTime, @Value("${jwt.refresh-expired-time}") long refreshTokenExpireTime) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    @Transactional
    public LoginResponse login(LoginServiceRequest dto) {
        User user = getUser(dto);

        String accessToken = tokenGenerator.generate(user.getId().toString(), user.getRole(),
                TokenType.ACCESS, Instant.now().plusSeconds(accessTokenExpireTime));
        String refreshToken = tokenGenerator.generate(user.getId().toString(), user.getRole(),
                TokenType.REFRESH, Instant.now().plusSeconds(refreshTokenExpireTime));

        refreshTokenRepository.save(new RefreshToken(refreshToken, user));

        return new LoginResponse(accessToken, refreshToken);
    }

    private User getUser(LoginServiceRequest dto) {
        return userRepository.findByEmail(dto.email())
                .filter(authUser -> passwordEncoder.matches(dto.password(), authUser.getEncodedPassword()))
                .orElseThrow(() -> new LoginFailException("이메일 혹은 비밀번호가 잘못되었습니다."));
    }


}
