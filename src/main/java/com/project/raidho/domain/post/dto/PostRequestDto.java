package com.project.raidho.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostRequestDto {
    private String content;
    private List<MultipartFile> imgUrl;
    private List<String> tags;
    private List<String> locationTags;
}
