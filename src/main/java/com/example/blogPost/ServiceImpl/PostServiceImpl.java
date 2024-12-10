package com.example.blogPost.ServiceImpl;

import com.example.blogPost.config.LoginUtil;
import com.example.blogPost.model.Comment;
import com.example.blogPost.model.Post;
import com.example.blogPost.model.Users;
import com.example.blogPost.repository.CommentRepo;
import com.example.blogPost.repository.PostRepo;
import com.example.blogPost.repository.UsersRepo;
import com.example.blogPost.service.PostService;
import com.example.blogPost.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private CommentRepo commentRepo;

    @Override
    public void createAndUpdatePost(Post post, MultipartFile thumbnail) throws IOException {
        String thumbnailUrl = fileUtils.uploadFileToCloudinary(thumbnail);

        if (post.getId()!=null){
            Post existedPost = postRepo.findById(post.getId())
                    .orElseThrow(()-> new RuntimeException("Post not found with the Id: "+post.getId()));
            existedPost.setTitle(post.getTitle());
            existedPost.setContent(post.getContent());

            String publicId = fileUtils.extractPublicIdFromUrl(existedPost.getThumbnailUrl());
            fileUtils.deleteFileFromCloudinary(publicId);

            existedPost.setThumbnailUrl(thumbnailUrl);
            existedPost.setUpdatedAt(Timestamp.from(Instant.now()));
            postRepo.save(existedPost);
        }else {

            Users user = userRepo.findById(loginUtil.getCurrentUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            post.setUsers(user);
            post.setCreatedAt(Timestamp.from(Instant.now()));
            post.setThumbnailUrl(thumbnailUrl);

            postRepo.save(post);
        }

    }

    @Override
    public List<Post> getAllPosts() {
        return postRepo.findAll();
    }

    @Override
    public Post getPostById(Long id) {
        return postRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }


    @Override
    public void deletePost(Long id) {
        List<Comment> commentList = commentRepo.findByPostId(id);
        for (Comment comment : commentList) {
            commentRepo.deleteById(comment.getId());
        }
        postRepo.deleteById(id);
    }
}
