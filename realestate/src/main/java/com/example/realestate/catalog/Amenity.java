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
@Table(name = "amenities")
public class Amenity extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;
}
