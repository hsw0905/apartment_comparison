package me.harry.apartment_comparison.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.apartment_comparison.domain.model.common.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Entity
public class User extends BaseEntity {
    @EmbeddedId
    private UserId id;

    @Column(name = "email", length = 200)
    private String email;

    @Column(name = "password", length = 100)
    private String encodedPassword;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "role", length = 20)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_out")
    private boolean isOut;

    @Column(name = "is_available")
    private boolean isAvailable;

    @Builder
    public User(UserId id, String email, String encodedPassword, String name, UserRole role, boolean isOut, boolean isAvailable) {
        this.id = id;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.name = name;
        this.role = role;
        this.isOut = isOut;
        this.isAvailable = isAvailable;
    }
}
