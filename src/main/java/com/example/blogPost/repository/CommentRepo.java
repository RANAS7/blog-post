package com.example.blogPost.repository;

import com.example.blogPost.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepo extends JpaRepository<Comment, UUID> {
    List<Comment> findByPost_PostId(UUID postId);
}
