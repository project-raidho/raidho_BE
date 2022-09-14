package com.project.raidho.controller;

import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/postheart")
public class PostHeartController {

    private final PostHeartService postHeartService;

    @PostMapping("/{postId}")
    public ResponseDto<?> postHeart(@PathVariable("postId") Long postId) {
        return postHeartService.createPostHeart(postId);
    }

    @DeleteMapping("/{postId}")
    public ResponseDto<?> postHeartDelete(@PathVariable("postId") Long postId) {
        return postHeartService.deletePostHeart(postId);
    }
}
