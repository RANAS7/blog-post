package com.example.blogPost.service;

import com.example.blogPost.model.Comment;
import com.example.blogPost.model.Post;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    void addComment(Comment comment);

    List<Comment> getComments(Long postId);

    void deleteComment(Long commentId);
}
