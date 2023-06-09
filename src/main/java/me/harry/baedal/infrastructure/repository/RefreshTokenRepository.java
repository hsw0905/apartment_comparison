package me.harry.baedal.infrastructure.repository;

import me.harry.baedal.domain.model.user.RefreshToken;
import me.harry.baedal.domain.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUser(User user);

    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
