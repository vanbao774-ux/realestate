package com.example.realestate.posts;

import com.example.realestate.catalog.Amenity;
import com.example.realestate.catalog.District;
import com.example.realestate.catalog.FurnishingLevel;
import com.example.realestate.catalog.Orientation;
import com.example.realestate.catalog.PropertyType;
import com.example.realestate.catalog.Province;
import com.example.realestate.catalog.Ward;
import com.example.realestate.common.BaseEntity;
import com.example.realestate.users.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PostPurpose purpose;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_type_id")
    private PropertyType propertyType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @Column(length = 255)
    private String address;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    private Integer bedrooms;

    private Integer bathrooms;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientation_id")
    private Orientation orientation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "furnishing_level_id")
    private FurnishingLevel furnishingLevel;

    @Column(length = 64)
    private String legalStatus;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private PostStatus status = PostStatus.DRAFT;

    @Column(nullable = false)
    private long viewsTotal = 0L;

    private java.time.Instant expiresAt;

    @Column(length = 255)
    private String rejectReason;

    private java.time.Instant featuredUntil;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PostMedia> media = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(
        name = "post_amenities",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
}
