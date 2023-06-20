package me.harry.baedal.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    @EmbeddedId
    private RefreshTokenId refreshTokenId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id")
    private User user;

    @Column(name = "refresh_token", length = 280)
    private String refreshToken;

    public RefreshToken(RefreshTokenId refreshTokenId, User user, String refreshToken) {
        this.refreshTokenId = refreshTokenId;
        this.user = user;
        this.refreshToken = refreshToken;
    }

    public void changeRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
