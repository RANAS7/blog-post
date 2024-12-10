package com.example.blogPost.service;

import com.example.blogPost.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PostService {
    void createAndUpdatePost(Post post, MultipartFile thumbnail) throws IOException;

    public abstract List<Post> getAllPosts();

    public abstract Post getPostById(UUID id);

    public abstract void deletePost(UUID id);
}
