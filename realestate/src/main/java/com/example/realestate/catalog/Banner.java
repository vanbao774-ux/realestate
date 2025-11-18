package com.example.realestate.catalog;

import com.example.realestate.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "banners")
public class Banner extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(length = 255)
    private String targetUrl;

    private boolean active = true;

    private Integer sortOrder;
}
