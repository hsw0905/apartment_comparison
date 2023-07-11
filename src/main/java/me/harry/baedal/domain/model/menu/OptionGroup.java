package me.harry.baedal.domain.model.menu;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.harry.baedal.domain.model.common.BaseEntity;

@Getter
@Entity
@Table(name = "option_groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionGroup extends BaseEntity {
    @EmbeddedId
    private OptionGroupId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menus_id")
    private Menu menu;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "is_basic_choice", nullable = false)
    private boolean isBasicChoice;

    @Column(name = "is_exclusive_choice", nullable = false)
    private boolean isExclusiveChoice;

    @Builder
    public OptionGroup(OptionGroupId id, Menu menu, String name, boolean isBasicChoice, boolean isExclusiveChoice) {
        this.id = id;
        this.menu = menu;
        this.name = name;
        this.isBasicChoice = isBasicChoice;
        this.isExclusiveChoice = isExclusiveChoice;
    }
}
