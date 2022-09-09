package com.project.raidho.controller;

import com.project.raidho.dto.request.ContentRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@RequestBody ContentRequestDto contentRequestDto) {
        return postService.createPost(contentRequestDto);
    }
}
