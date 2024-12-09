package com.example.blogPost.service;

import com.example.blogPost.model.Post;

import java.util.List;
import java.util.UUID;

public interface PostService {
    void createAndUpdatePost(Post post);

    public abstract List<Post> getAllPosts();

    public abstract Post getPostById(UUID id);

    public abstract void deletePost(UUID id);
}
