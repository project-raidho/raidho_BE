package com.project.raidho.service;

import com.project.raidho.domain.Post;
import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@Builder
@RequiredArgsConstructor
public class PostService {
    @Transactional
    public ResponseDto<?> CreatPost(PostRequestDto postRequestDto, MultipartFile multipartFile, HttpServletRequest request ) throws IOException {


        Post post = Post.builder().build()
                .getContent()


    }
}
