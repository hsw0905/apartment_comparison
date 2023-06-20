package me.harry.baedal.domain.model;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class RefreshTokenId extends EntityId implements Serializable {
    protected RefreshTokenId() {
        super();
    }

    public RefreshTokenId(String value) {
        super(value);
    }

    public static RefreshTokenId generate() {
        return new RefreshTokenId(generateId());
    }
}
