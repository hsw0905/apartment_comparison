package me.harry.baedal.domain.model.menu;

import jakarta.persistence.Embeddable;
import me.harry.baedal.domain.model.common.EntityId;

import java.io.Serializable;

@Embeddable
public class MenuId extends EntityId implements Serializable {
    protected MenuId() {
        super();
    }

    public MenuId(String value) {
        super(value);
    }

    public static MenuId generate() {
        return new MenuId(generateId());
    }
}
