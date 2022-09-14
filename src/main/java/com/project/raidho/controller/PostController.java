package com.project.raidho.controller;

import com.project.raidho.domain.member.Member;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.security.PrincipalDetails;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails
                                        ) {

        Member member = ((PrincipalDetails) userDetails).getMember();
        System.out.println(member.getProviderId());
        System.out.println(member.getMemberName());

        Member member1 = principalDetails.getMember();
        System.out.println(member1.getMemberName());
        System.out.println(member1.getProviderId());

        return postService.getAllPost(page,size);
    }
}