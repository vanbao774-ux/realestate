package com.example.realestate.posts;

import com.example.realestate.posts.PostStatus;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    long countByOwnerIdAndStatus(Long ownerId, PostStatus status);

    @Query("select coalesce(sum(p.viewsTotal),0) from Post p where p.owner.id = :ownerId")
    long sumViewsByOwner(@Param("ownerId") Long ownerId);

    @Modifying
    @Query("update Post p set p.status = com.example.realestate.posts.PostStatus.EXPIRED where p.status <> com.example.realestate.posts.PostStatus.EXPIRED and p.expiresAt is not null and p.expiresAt < :now")
    int expirePosts(@Param("now") Instant now);
}
