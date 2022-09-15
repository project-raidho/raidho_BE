package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/postheart")
public class PostHeartController {

    private final PostHeartService postHeartService;

    @PostMapping("/{postId}")
    public ResponseDto<?> postHeart(@PathVariable("postId") Long postId, HttpServletRequest request) {
        return postHeartService.createPostHeart(postId, request);
    }

    @DeleteMapping("/{postId}")
    public ResponseDto<?> postHeartDelete(@PathVariable("postId") Long postId, HttpServletRequest request) {
        return postHeartService.deletePostHeart(postId, request);
    }
}
