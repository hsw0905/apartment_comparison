package me.harry.baedal.infrastructure.repository;

import me.harry.baedal.domain.model.User;
import me.harry.baedal.domain.model.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(String email);
}
