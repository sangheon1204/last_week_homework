package com.sparta.homework.dto;

import com.sparta.homework.entity.Comment;
import com.sparta.homework.entity.Post;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> commentList = new ArrayList<>();

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        username = post.getUsername();
        likes = post.getLikes();
        createdAt = post.getCreatedAt();
        modifiedAt = post.getModifiedAt();
        commentList = commentResponseDtos;
    }
    public PostResponseDto(Post post) {
        id = post.getId();
        title = post.getTitle();
        content = post.getContent();
        username = post.getUsername();
        likes = post.getLikes();
        createdAt = post.getCreatedAt();
        modifiedAt = post.getModifiedAt();
    }
}

