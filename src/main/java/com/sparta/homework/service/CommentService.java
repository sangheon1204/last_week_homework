package com.sparta.homework.service;

import com.sparta.homework.dto.CommentRequestDto;
import com.sparta.homework.dto.CommentResponseDto;
import com.sparta.homework.dto.ResponseDto;
import com.sparta.homework.entity.Comment;
import com.sparta.homework.entity.Post;
import com.sparta.homework.entity.User;
import com.sparta.homework.entity.UserRoleEnum;
import com.sparta.homework.repository.CommentRepository;
import com.sparta.homework.repository.PostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;


    //댓글 작성
    @Transactional
    public CommentResponseDto createComment(Long id, CommentRequestDto commentRequestDto, User user) {
        Post post = postRepository.findById(id).orElseThrow(
                ()->new IllegalArgumentException("게시글이 존재하지 않습니다.")
        );
        Comment comment = new Comment(commentRequestDto,post);
        commentRepository.save(comment);
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
        return commentResponseDto;
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto commentRequestDto, User user) {
        Post post;
        Comment comment;
        if(user.getRole() == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("게시글이 존재하지 않습니다.")
            );
            comment = (Comment) commentRepository.findByIdAndPost(id,post).orElseThrow(
                    () ->new IllegalArgumentException("댓글이 존재하지 않습니다.")
            );

        }else {
            post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
            );
            comment = (Comment) commentRepository.findByIdAndPost(id,post).orElseThrow(
                    () ->new IllegalArgumentException("댓글이 존재하지 않습니다.")
            );
            if(!(post.getCommentList().get((id.intValue()-1)).equals(comment))) {
                throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
            }
        }
        comment.update(commentRequestDto);
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
        return commentResponseDto;
    }

    @Transactional
    public ResponseDto deleteComment(Long id, User user) {
        Post post;
        Comment comment;
        if(user.getRole() == UserRoleEnum.ADMIN) {
            post = postRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("게시물이 존재하지 않습니다.")
            );
            comment = (Comment)commentRepository.findByIdAndPost(id,post).orElseThrow(
                    ()-> new IllegalArgumentException("댓글이 존재하지 않습니다.")
            );

        }else {
            post = postRepository.findByIdAndUsername(id, user.getUsername()).orElseThrow(
                    () -> new IllegalArgumentException("게시글이 존재하지 않습니다.")
            );
            comment = (Comment)commentRepository.findByIdAndPost(id,post).orElseThrow(
                    ()-> new IllegalArgumentException("댓글이 존재하지 않습니다.")
            );
            if(!(post.getCommentList().get((id.intValue()-1)).equals(comment))) {
                throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
            }
        }
        commentRepository.delete(comment);
        ResponseDto responseDto = new ResponseDto("댓글 삭제 성공", HttpStatus.OK.value());
        return responseDto;
    }
}
