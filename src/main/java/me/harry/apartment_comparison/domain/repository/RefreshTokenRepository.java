package me.harry.apartment_comparison.domain.repository;

import me.harry.apartment_comparison.domain.model.RefreshToken;
import me.harry.apartment_comparison.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> deleteByUser(User user);
    Optional<RefreshToken> findByUser(User user);
}
