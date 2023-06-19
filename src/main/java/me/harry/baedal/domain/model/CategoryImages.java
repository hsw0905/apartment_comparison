package me.harry.baedal.domain.model;

import jakarta.persistence.*;
import me.harry.baedal.domain.model.common.BaseEntity;

@Entity
@Table(name = "category_images")
public class CategoryImages extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categories_id")
    private Category category;

    @Column(name = "url", length = 300)
    private String url;
}
