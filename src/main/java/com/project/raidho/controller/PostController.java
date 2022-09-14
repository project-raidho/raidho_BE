package com.project.raidho.controller;

import com.project.raidho.dto.request.PostRequestDto;
import com.project.raidho.dto.resposnse.ResponseDto;
import com.project.raidho.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    @PostMapping
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {

        System.out.println("request.getHeader(\"Authorization\") = " + request.getHeader("Authorization"));
        System.out.println(request.getHeader("authorization"));
        Enumeration params = request.getParameterNames();
        System.out.println("======================================");
        while(params.hasMoreElements()) {
            String name = (String) params.nextElement();
            System.out.println(name + " : " + request.getParameter(name));
        }
        System.out.println("======================================");

        postService.createPost(postRequestDto, request);
        return ResponseDto.success("ok");
    }
    @GetMapping("/latest")
    public ResponseDto<?> getAllPost(@RequestParam (value = "page",defaultValue = "0")int page,
                                     @RequestParam (value = "size",defaultValue = "20")int size) {
        return postService.getAllPost(page,size);
    }
}