package me.harry.apartment_comparison.application.service;

import me.harry.apartment_comparison.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LogoutService {
    private final RefreshTokenRepository refreshTokenRepository;

    public LogoutService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void logout(String authUserId) {
        refreshTokenRepository.deleteByUserId(authUserId);
    }
}
