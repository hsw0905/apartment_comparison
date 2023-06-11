package me.harry.apartment_comparison.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
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

    @Column(name = "name")
    private String name;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "is_out")
    private boolean isOut;

    @Column(name = "is_available")
    private boolean isAvailable;
}
