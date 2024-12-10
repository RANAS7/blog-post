package com.example.blogPost.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)
    private Users users;

    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
}
