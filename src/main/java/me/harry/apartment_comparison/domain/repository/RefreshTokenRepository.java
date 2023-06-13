package me.harry.apartment_comparison.domain.repository;

import me.harry.apartment_comparison.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
