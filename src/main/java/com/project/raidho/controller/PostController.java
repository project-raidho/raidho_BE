package com.project.raidho.controller;

import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto) throws IOException {
        postService.createPost(postRequestDto);
        return ResponseDto.success("ok");
    }
}