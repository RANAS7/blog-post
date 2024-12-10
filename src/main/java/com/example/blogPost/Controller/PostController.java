package com.example.blogPost.Controller;

import com.example.blogPost.model.Post;
import com.example.blogPost.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<String> createOrUpdatePost(
            @ModelAttribute Post post,
            @RequestParam MultipartFile thumbnail) throws IOException {
        postService.createAndUpdatePost(post, thumbnail);
        return ResponseEntity.ok("The post was created or updated successfully!");
    }



    @GetMapping("/")
    public ResponseEntity<?> getPostById(@RequestParam(required = false) Long id) {
        if (id != null) {
            return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(postService.getAllPosts(), HttpStatus.OK);
        }
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deletePost(@RequestParam Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>("The post deleted successfully", HttpStatus.OK);
    }
}
