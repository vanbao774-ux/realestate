package com.example.realestate.posts;

import com.example.realestate.auth.UserPrincipal;
import com.example.realestate.posts.dto.PostSummary;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{postId}")
    public void addFavorite(@PathVariable Long postId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.addFavorite(postId, principal.getUser());
    }

    @DeleteMapping("/{postId}")
    public void removeFavorite(@PathVariable Long postId, @AuthenticationPrincipal UserPrincipal principal) {
        favoriteService.removeFavorite(postId, principal.getUser());
    }

    @GetMapping
    public List<PostSummary> myFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.favorites(principal.getUser());
    }
}
