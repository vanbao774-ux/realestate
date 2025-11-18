package com.example.realestate.posts;

import com.example.realestate.common.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PostMediaService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final Path uploadDirectory;

    public PostMediaService(
        PostRepository postRepository,
        PostMediaRepository postMediaRepository,
        @Value("${app.storage.upload-dir:uploads}") String uploadDir
    ) {
        this.postRepository = postRepository;
        this.postMediaRepository = postMediaRepository;
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @Transactional
    public List<PostMedia> upload(Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        if (files.size() < 1) {
            throw new IllegalArgumentException("At least one media file is required");
        }
        if (post.getMedia().size() + files.size() > 24) {
            throw new IllegalArgumentException("Maximum 24 media files per post");
        }
        ensureDirectory();
        int sortOrder = post.getMedia().size();
        for (MultipartFile file : files) {
            String filename = saveFile(file);
            PostMedia media = new PostMedia();
            media.setPost(post);
            media.setUrl("/" + uploadDirectory.getFileName() + "/" + filename);
            media.setMediaType(detectType(file));
            media.setSortOrder(++sortOrder);
            post.getMedia().add(media);
        }
        return postMediaRepository.findByPostIdOrderBySortOrderAsc(postId);
    }

    @Transactional
    public void deleteMedia(Long postId, Long mediaId) {
        PostMedia media = postMediaRepository.findById(mediaId)
            .orElseThrow(() -> new ResourceNotFoundException("Media not found"));
        if (!media.getPost().getId().equals(postId)) {
            throw new IllegalArgumentException("Media does not belong to post");
        }
        postMediaRepository.delete(media);
    }

    private void ensureDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    private String saveFile(MultipartFile file) {
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = LocalDate.now() + "-" + System.nanoTime() + (extension != null ? "." + extension : "");
        Path destination = uploadDirectory.resolve(filename);
        try {
            file.transferTo(destination);
            return filename;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store file", e);
        }
    }

    private String detectType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("video")) {
            return "video";
        }
        return "image";
    }
}
