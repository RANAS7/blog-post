package com.example.blogPost.ServiceImpl;

import com.example.blogPost.config.LoginUtil;
import com.example.blogPost.model.Comment;
import com.example.blogPost.model.Post;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.CommentRepo;
import com.example.blogPost.repository.PostRepo;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Override
    public void addComment(Comment comment) {
        Post post = postRepo.findById(comment.getPost().getId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Users user = userRepo.findById(loginUtil.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        comment.setPost(post);
        comment.setUsers(user);
        comment.setCreatedAt(Timestamp.from(Instant.now()));
        commentRepo.save(comment);
    }

    @Override
    public List<Comment> getComments(UUID postId) {
        return commentRepo.findByPostId(postId);
    }

    @Override
    public void deleteComment(UUID commentId) {
        commentRepo.deleteById(commentId);
    }
}
