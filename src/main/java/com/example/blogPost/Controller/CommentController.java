package com.example.blogPost.Controller;

import com.example.blogPost.model.Comment;
import com.example.blogPost.model.Post;
import com.example.blogPost.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return new ResponseEntity<>("Your comment was added successfully!",HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getComments(@RequestParam Long postId) {
        return new ResponseEntity<>(commentService.getComments(postId),HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteComment(@RequestParam Long id){
        commentService.deleteComment(id);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }
}
