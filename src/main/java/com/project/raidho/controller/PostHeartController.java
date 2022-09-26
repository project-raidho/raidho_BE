package com.project.raidho.controller;

import com.project.raidho.exception.RaidhoException;
import com.project.raidho.service.PostHeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/postheart")
public class PostHeartController {

    private final PostHeartService postHeartService;

    @PostMapping("/{postId}")
    public ResponseEntity<?> postHeart(@PathVariable("postId") Long postId, HttpServletRequest request) throws RaidhoException {
        return postHeartService.createPostHeart(postId, request);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> postHeartDelete(@PathVariable("postId") Long postId, HttpServletRequest request) throws RaidhoException {
        return postHeartService.deletePostHeart(postId, request);
    }
}
