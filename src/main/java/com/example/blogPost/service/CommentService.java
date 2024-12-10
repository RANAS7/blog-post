package com.example.blogPost.service;

import com.example.blogPost.model.Comment;
import com.example.blogPost.model.Post;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    void addComment(Comment comment);

    List<Comment> getComments(UUID postId);

    void deleteComment(UUID commentId);
}
