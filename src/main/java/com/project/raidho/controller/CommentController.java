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

    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(@PathVariable Long postId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody CommentRequestDto commentRequestDto)throws RaidhoException {
        return ResponseEntity.ok().body(commentService.createComment(postId,userDetails,commentRequestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAllCommentsByPost(@PathVariable Long id,
                                                  @RequestParam (value = "page",defaultValue = "0")int page,
                                                  @RequestParam (value = "size",defaultValue = "15")int size,
                                                  @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(commentService.getAllCommentsByPost(id,userDetails,page,size));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails, @RequestBody UserDetailsCommentDto userDetailsCommentDto){
        return commentService.updateComment(commentId,userDetails,userDetailsCommentDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deletePost(@PathVariable("commentId") Long commentId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(commentService.deleteComment(commentId, userDetails));
    }
}
