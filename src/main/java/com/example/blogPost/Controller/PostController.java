package com.example.blogPost.Controller;

import com.example.blogPost.model.Post;
import com.example.blogPost.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(@ModelAttribute Post post, @RequestParam MultipartFile thumbnail) {
        postService .createAndUpdatePost(post, thumbnail);
        if (post.getId() != null) {
            return new ResponseEntity<>("The post updated successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Post created successfully", HttpStatus.CREATED);

        }
    }


    @GetMapping("/")
    public ResponseEntity<?> getPostById(@RequestParam(required = false) UUID id) {
        if (id != null) {
            return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deletePost(@RequestParam UUID id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
