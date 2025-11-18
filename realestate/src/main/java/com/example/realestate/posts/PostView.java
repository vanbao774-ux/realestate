package com.example.realestate.posts;

import com.example.realestate.common.BaseEntity;
import com.example.realestate.users.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "post_views")
public class PostView extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_user_id")
    private User viewer;

    @Column(length = 45)
    private String viewerIp;

    @Column(nullable = false)
    private Instant viewedAt;
}
