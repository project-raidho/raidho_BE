package com.project.raidho.controller;

import com.project.raidho.domain.post.dto.PostRequestDto;
import com.project.raidho.domain.post.dto.UpdatePostRequestDto;
import com.project.raidho.domain.ResponseDto;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
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
    @PostMapping
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        postService.createPost(postRequestDto, request);
        return ResponseDto.success("ok");
    }
    //전체조회
    @GetMapping("/latest")
    public ResponseDto<?> getAllPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "20")int size,
                                     @AuthenticationPrincipal UserDetails userDetails
                                        ) {

        return postService.getAllPost(page,size,userDetails);
    }

    @GetMapping("/likelist")
    public ResponseDto<?> getAlllikePost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "5")int size,
                                     @AuthenticationPrincipal UserDetails userDetails
    ) {
        return postService.getAlllikePost(page,size,userDetails);
    }
    //단건조회

    @GetMapping("/{postId}")
    public ResponseDto<?> getPostDetail(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails){
     return postService.getPostDetail(userDetails, postId);
    }
    //삭제

    @DeleteMapping("/{postId}")
    public ResponseDto<?> deletePost(@PathVariable("postId") Long postId, @AuthenticationPrincipal UserDetails userDetails){
        return postService.deletePost(postId, userDetails);
    }
    //TODO :: 수정
    @PutMapping("/{postId}")
    public ResponseDto<?> updatePost(@PathVariable("postId") Long postId,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     UpdatePostRequestDto updatePostRequestDto) {
        return postService.updatePost(postId,userDetails,updatePostRequestDto);
    }


}