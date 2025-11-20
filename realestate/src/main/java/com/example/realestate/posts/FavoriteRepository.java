package com.example.realestate.posts;

import com.example.realestate.users.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserAndPost(User user, Post post);

    List<Favorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select count(f) from Favorite f where f.post.owner.id = :ownerId")
    long countFavoritesForOwner(@Param("ownerId") Long ownerId);
}
