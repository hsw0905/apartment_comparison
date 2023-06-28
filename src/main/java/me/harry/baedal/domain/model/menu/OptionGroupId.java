package me.harry.baedal.domain.model.menu;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class OptionGroupId extends EntityId implements Serializable {
    protected OptionGroupId() {
        super();
    }

    public OptionGroupId(String value) {
        super(value);
    }

    public static OptionGroupId generate() {
        return new OptionGroupId(generateId());
    }
}
