package com.example.realestate.posts;

import com.example.realestate.common.ResourceNotFoundException;
import com.example.realestate.users.User;
import com.example.realestate.posts.dto.PostSummary;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PostRepository postRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, PostRepository postRepository) {
        this.favoriteRepository = favoriteRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void addFavorite(Long postId, User user) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (favoriteRepository.findByUserAndPost(user, post).isPresent()) {
            return;
        }
        Favorite favorite = new Favorite();
        favorite.setPost(post);
        favorite.setUser(user);
        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long postId, User user) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        favoriteRepository.findByUserAndPost(user, post)
            .ifPresent(favoriteRepository::delete);
    }

    @Transactional
    public List<PostSummary> favorites(User user) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
            .map(Favorite::getPost)
            .map(PostMapper::toSummary)
            .collect(Collectors.toList());
    }

    public long countFavoritesForOwner(Long ownerId) {
        return favoriteRepository.countFavoritesForOwner(ownerId);
    }
}
