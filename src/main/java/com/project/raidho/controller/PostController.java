package com.project.raidho.controller;

import com.project.raidho.domain.member.Member;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.security.PrincipalDetails;
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
    @GetMapping("/latest")
    public ResponseDto<?> getAllPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "20")int size,
                                     @AuthenticationPrincipal UserDetails userDetails
                                        ) {

        return postService.getAllPost(page,size,userDetails);
    }

//    @GetMapping("/likelist")
//    public ResponseDto<?> getAlllikePost(@RequestParam (value = "page",defaultValue = "0")int page,
//                                     @RequestParam (value = "size",defaultValue = "20")int size,
//                                     @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        return postService.getAlllikePost(page,size,userDetails);
//    }
}