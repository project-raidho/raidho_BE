package com.project.raidho.controller;

import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostService;
import com.project.raidho.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    private final S3Service s3Service;

    @RequestMapping(value = "/api/post", method = RequestMethod.POST)
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto, @RequestPart("multipartFiles") List<MultipartFile> multipartFiles) {
        List<String> imgPaths = s3Service.upload(multipartFiles);
        return postService.createPost(postRequestDto, imgPaths);

    }
}