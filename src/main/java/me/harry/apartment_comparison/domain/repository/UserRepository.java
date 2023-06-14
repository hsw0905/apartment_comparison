package me.harry.apartment_comparison.domain.repository;

import me.harry.apartment_comparison.domain.model.User;
import me.harry.apartment_comparison.domain.model.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(String email);
}
