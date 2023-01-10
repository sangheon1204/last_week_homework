package com.sparta.homework.service;

import com.sparta.homework.dto.*;
import com.sparta.homework.entity.Comment;
import com.sparta.homework.entity.Post;
import com.sparta.homework.entity.User;
import com.sparta.homework.entity.UserRoleEnum;
import com.sparta.homework.jwt.JwtUtil;
import com.sparta.homework.repository.CommentRepository;
import com.sparta.homework.repository.PostRepository;
import com.sparta.homework.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    //게시글 작성
    @Transactional
    public PostResponseDto createPost(PostRequestDto requestDto, User user) {
            Post post = new Post(requestDto, user.getUsername(), user);
            postRepository.save(post);
            PostResponseDto postResponseDto = new PostResponseDto(post);
            return postResponseDto;
    }

    //전체 목록 조회
    @Transactional
    public PostResponseDtoList getPosts() {
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> postResponseDtoList = new ArrayList<>();
        for(Post post : postList) {
            List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
            List<Comment> commentList = commentRepository.findAllByPostOrderByModifiedAtDesc(post);
            for(Comment comment : commentList) {
                commentResponseDtos.add(new CommentResponseDto(comment));
            }
            postResponseDtoList.add(new PostResponseDto(post,commentResponseDtos));
        }
        PostResponseDtoList postResponseDtoList1 = new PostResponseDtoList(postResponseDtoList);
        return postResponseDtoList1;
    }

    @Transactional
    public PostResponseDto getPostsById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(
                ()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다.")
        );
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        List<Comment> commentList = commentRepository.findAllByPostOrderByModifiedAtDesc(post);
        for(Comment comment : commentList) {
            commentResponseDtos.add(new CommentResponseDto(comment));
        }
        PostResponseDto getResponseDto = new PostResponseDto(post,commentResponseDtos);
        return getResponseDto;
    }

    @Transactional
    public PostResponseDto update(Long id, PostRequestDto requestDto, User user) {
        Post post = null;
        if(user.getRole() == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
            );
        }else {
            post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다.")
            );
        }
        post.update(requestDto);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for (Comment comment : post.getCommentList()) {
            commentResponseDtos.add(new CommentResponseDto(comment));
        }
        PostResponseDto getResponseDto = new PostResponseDto(post, commentResponseDtos);
        return getResponseDto;
    }

    @Transactional
    public ResponseDto delete(Long id,User user) {
        Post post = null;
        if(user.getRole() == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    ()-> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );
        }else {
            post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new NullPointerException("해당 게시글은 존재하지 않습니다.")
            );
        }
        postRepository.delete(post);
        ResponseDto responseDto = new ResponseDto("게시글 삭제 성공",HttpStatus.OK.value());
        return responseDto;
    }
}
