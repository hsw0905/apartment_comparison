package me.harry.baedal.infrastructure.repository;

import me.harry.baedal.domain.model.user.User;
import me.harry.baedal.domain.model.user.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UserId> {
    Optional<User> findByEmail(String email);
}
