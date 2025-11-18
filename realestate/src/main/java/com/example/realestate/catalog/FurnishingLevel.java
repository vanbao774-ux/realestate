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
@Table(name = "furnishing_levels")
public class FurnishingLevel extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;
}
