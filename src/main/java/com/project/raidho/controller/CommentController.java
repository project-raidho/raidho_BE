package com.project.raidho.controller;

import com.project.raidho.domain.comment.dto.CommentRequestDto;
import com.project.raidho.domain.comment.dto.UserDetailsCommentDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentRequestDto commentRequestDto)throws RaidhoException {
        return ResponseEntity.ok().body(commentService.createComment(postId,userDetails,commentRequestDto));
    }
    // 게시글 내에 있는 전체 댓글 가져오기
    @GetMapping("/{postId}")
    public ResponseEntity<?> getAllCommentsByPost(@PathVariable Long postId,
                                                  @RequestParam (value = "page",defaultValue = "0")int page,
                                                  @RequestParam (value = "size",defaultValue = "15")int size,
                                                  @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(commentService.getAllCommentsByPost(postId,userDetails,page,size));
    }
    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody UserDetailsCommentDto userDetailsCommentDto){
        return commentService.updateComment(commentId,userDetails,userDetailsCommentDto);
    }
    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deletePost(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(commentService.deleteComment(commentId, userDetails));
    }
}
