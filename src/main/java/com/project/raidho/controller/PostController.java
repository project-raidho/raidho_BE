package com.project.raidho.controller;

import com.project.raidho.dto.request.ContentRequestDto;
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
    public ResponseDto<?> createPost(@RequestPart("content") ContentRequestDto contentRequestDto, @RequestPart("imgUrl") List<MultipartFile> multipartFiles) {
        System.out.println("9384723984732984792384729384");
        List<String> imgPaths = s3Service.upload(multipartFiles);
        return postService.createPost(contentRequestDto, imgPaths);
    }
}
