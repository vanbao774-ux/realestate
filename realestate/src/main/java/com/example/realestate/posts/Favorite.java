package com.example.realestate.posts;

import com.example.realestate.common.BaseEntity;
import com.example.realestate.users.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "favorites", uniqueConstraints = @jakarta.persistence.UniqueConstraint(name = "uk_favorites_user_post", columnNames = { "user_id", "post_id" }))
public class Favorite extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;
}
