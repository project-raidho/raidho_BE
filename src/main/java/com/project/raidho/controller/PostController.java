package com.project.raidho.controller;

import com.project.raidho.domain.post.dto.PostRequestDto;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    // 게시글 등록
    @PostMapping
    public ResponseEntity<?> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws RaidhoException, IOException {
        return ResponseEntity.ok().body(ResponseDto.success(postService.createPost(postRequestDto, request)));
    }
    //전체조회
    @GetMapping("/latest")
    public ResponseEntity<?> getAllPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "5")int size,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(postService.getAllPost(page,size,userDetails));
    }

    @GetMapping("/likelist")
    public ResponseEntity<?> getAlllikePost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "5")int size,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(postService.getAlllikePost(page,size,userDetails));
    }

    @GetMapping("/mypost")
    public ResponseEntity<?> getAllMyPost(@AuthenticationPrincipal UserDetails userDetails) {
        return postService.getAllMyPost(userDetails);
    }

    //단건조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
     return ResponseEntity.ok().body(postService.getPostDetail(userDetails, postId));
    }

    //삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails) throws RaidhoException {
        return ResponseEntity.ok().body(postService.deletePost(postId, userDetails));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails, UpdatePostRequestDto updatePostRequestDto) throws RaidhoException {
        return postService.updatePost(postId,userDetails,updatePostRequestDto);
    }


}