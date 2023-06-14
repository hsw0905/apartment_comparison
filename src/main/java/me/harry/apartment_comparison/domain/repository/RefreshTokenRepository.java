package me.harry.apartment_comparison.domain.repository;

import me.harry.apartment_comparison.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> deleteByUserId(String userId);
    Optional<RefreshToken> findByUserId(String userId);
}
