package com.project.raidho.controller;

import com.project.raidho.domain.ResponseDto;
import com.project.raidho.service.S3Service;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
public class EditMemberController {
    private final S3Service s3Service;

    @PutMapping
    public ResponseDto<?> editMyPage (MultipartFile multipartFile,String fileName) throws IOException {

        ResponseDto.success(s3Service.editMyPage(fileName));
        ResponseDto.success(s3Service.upload(multipartFile));
        return ResponseDto.success("이미지 수정 성공");
    }
}
