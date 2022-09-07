package com.project.raidho.controller;

import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Member;
import java.util.function.Function;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    @PostMapping
    public ResponseDto<?> updatePost(PostRequestDto postRequestDto){
        MultipartFile multipartFile = postRequestDto.getImgFile();
    }
}
