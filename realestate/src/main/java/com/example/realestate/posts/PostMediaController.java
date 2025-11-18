package com.example.realestate.posts;

import com.example.realestate.posts.dto.PostMediaResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts/{postId}/media")
public class PostMediaController {

    private final PostMediaService postMediaService;

    public PostMediaController(PostMediaService postMediaService) {
        this.postMediaService = postMediaService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<PostMediaResponse> upload(
        @PathVariable Long postId,
        @RequestParam("files") List<MultipartFile> files
    ) {
        return postMediaService.upload(postId, files).stream()
            .map(media -> new PostMediaResponse(media.getId(), media.getUrl(), media.getMediaType(), media.getSortOrder()))
            .collect(Collectors.toList());
    }

    @DeleteMapping("/{mediaId}")
    public void delete(@PathVariable Long postId, @PathVariable Long mediaId) {
        postMediaService.deleteMedia(postId, mediaId);
    }
}
