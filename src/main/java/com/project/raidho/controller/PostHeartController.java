//package com.project.raidho.controller;
//
//import com.project.raidho.dto.resposnse.ResponseDto;
//import com.project.raidho.service.PostHeartService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RequiredArgsConstructor
//@RestController
//public class PostHeartController {
//
//    private final PostHeartService postHeartService;
//
//    @PostMapping("/api/postLike/{postId}")
//    public ResponseDto<?> postHeart(@PathVariable("postId") Long postId, HttpServletRequest httpServletRequest) {
//        return postHeartService.createPostHeart(postId, httpServletRequest);
//    }
//
//    @DeleteMapping("/api/postLike/{postId}")
//    public ResponseDto<?> postHeartDelete(@PathVariable("postId") Long postId, HttpServletRequest httpServletRequest) {
//        return postHeartService.deletePostHeart(postId, httpServletRequest);
//    }
//}
