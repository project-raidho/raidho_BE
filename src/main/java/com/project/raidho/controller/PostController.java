package com.project.raidho.controller;

import com.project.raidho.domain.member.Member;
import com.project.raidho.dto.request.ContentRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private Member member;

    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestBody ContentRequestDto contentRequestDto,HttpServletRequest httpServletRequest) {
        return postService.createPost(contentRequestDto,httpServletRequest);
    }
}
