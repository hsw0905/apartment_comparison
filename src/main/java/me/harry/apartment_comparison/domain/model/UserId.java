package me.harry.apartment_comparison.domain.model;

import jakarta.persistence.Embeddable;
import me.harry.apartment_comparison.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class UserId extends EntityId implements Serializable {
    protected UserId() {
        super();
    }

    public UserId(String value) {
        super(value);
    }

    public static UserId generate() {
        return new UserId(generateId());
    }
}
